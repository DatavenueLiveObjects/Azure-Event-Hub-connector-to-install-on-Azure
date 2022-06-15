/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.evthub;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.orange.lo.sample.mqtt2eventhub.utils.Counters;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

@Configuration
public class EventHubConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private EventHubClient ehClient;
    private Counters counters;

    private ThreadPoolExecutor tpe;
    private RetryPolicy<Void> retryPolicy;

    public EventHubConfig(EventHubProperties eventHubProperties, Counters counters,
                          EventHubClientFactory clientFactory, ThreadPoolExecutor threadPoolExecutor) throws IOException, EventHubException {
        tpe = threadPoolExecutor;
        ConnectionStringBuilder conn = new ConnectionStringBuilder()
                .setOperationTimeout(Duration.of(eventHubProperties.getConnectionTimeout(), ChronoUnit.MILLIS))
                .setNamespaceName(eventHubProperties.getNameSpace())
                .setEventHubName(eventHubProperties.getEvtHubName())
                .setSasKeyName(eventHubProperties.getSasKeyName())
                .setSasKey(eventHubProperties.getSasKey());

        this.counters = counters;

        Duration throttlingDelay = eventHubProperties.getThrottlingDelay();
        int maxAttempts = eventHubProperties.getMaxSendAttempts();

        retryPolicy = new RetryPolicy<Void>()
                .withMaxAttempts(maxAttempts)
                .withBackoff(throttlingDelay.toNanos(), throttlingDelay.multipliedBy(1 << maxAttempts).toNanos(), ChronoUnit.NANOS)
                .withMaxDuration(Duration.ofHours(1))
                .handle(EventHubException.class)
                .onRetry(attempt -> counters.getMesasageSentAttemptCounter().increment())
                .onRetriesExceeded(e -> {
                    LOG.error("too many retry attempts, aborting");
                    counters.getMesasageSentFailedCounter().increment();
                })
                .onFailedAttempt(attempt -> {
                    counters.getMesasageSentAttemptFailedCounter().increment();
                    LOG.error(attempt.getLastFailure().getMessage());
                })
                .abortOn(InterruptedException.class)
                .onAbort(msg -> {
                    LOG.error("interrupted while waiting for resend, aborting");
                    Thread.currentThread().interrupt();
                    counters.getMesasageSentFailedCounter().increment();
                });


        LOG.info("INIT HUB");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        ehClient = clientFactory.createEventHubClient(conn, scheduledExecutorService);
    }

    @Bean
    public EvtHubSender evtHubSender() {
        return msg -> {
            try {
                tpe.submit(() -> {
                    send(msg);
                });
            } catch (RejectedExecutionException rejected) {
                counters.getMesasageSentFailedCounter().increment();
                LOG.error("too many tasks in queue, rejecting: {}", msg.hashCode());
            }
        };
    }

    private void send(String msg) {
        counters.getMesasageSentAttemptCounter().increment();

        byte[] payloadBytes = msg.getBytes(Charset.defaultCharset());
        EventData sendEvent = EventData.create(payloadBytes);

        RetryPolicy<Void> sendRetryPolicy = retryPolicy
                .copy()
                .onRetry(attempt ->
                LOG.info("retrying to send ({}): {}", attempt.getAttemptCount(), msg.hashCode())
        );

        try {
            Failsafe.with(sendRetryPolicy)
                    .run(e -> {
                        ehClient.sendSync(sendEvent);
                        counters.getMesasageSentCounter().increment();
                    });
        } catch (RuntimeException e) {
            LOG.error(e.toString());
        }
    }

    @PreDestroy
    public void onDestroy() {
        LOG.info("shutting down executor service");
        tpe.shutdown();
        try {
            ehClient.closeSync();
        } catch (EventHubException e) {
            LOG.error(e.getMessage());
        }
    }

    @Scheduled(fixedRate = 30000)
    public void reportExecutorData() {
        LOG.info("pool size: {}, active threads: {}, tasks in queue: {}", tpe.getPoolSize(), tpe.getActiveCount(), tpe.getQueue().size());
    }
}
