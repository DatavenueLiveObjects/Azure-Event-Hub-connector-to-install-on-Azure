/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

@Configuration
public class EventHubConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private int maxAttempts;
    private EventHubClient ehClient;
    private Counters counters;

    private ThreadPoolExecutor tpe;
    private long throttlingDelay;

    public EventHubConfig(EventHubProperties eventHubProperties, Counters counters,
                          EventHubClientFactory clientFactory) throws IOException, EventHubException {
        ConnectionStringBuilder conn = new ConnectionStringBuilder()
                .setOperationTimeout(Duration.of(eventHubProperties.getConnectionTimeout(), ChronoUnit.MILLIS))
                .setNamespaceName(eventHubProperties.getNameSpace())
                .setEventHubName(eventHubProperties.getEvtHubName())
                .setSasKeyName(eventHubProperties.getSasKeyName())
                .setSasKey(eventHubProperties.getSasKey());

        this.counters = counters;

        BlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(eventHubProperties.getTaskQueueSize());
        tpe = new ThreadPoolExecutor(eventHubProperties.getThreadPoolSize(), eventHubProperties.getThreadPoolSize(), 10, TimeUnit.SECONDS, tasks);
        throttlingDelay = eventHubProperties.getThrottlingDelay();
        maxAttempts = eventHubProperties.getMaxSendAttempts();

        LOG.info("INIT HUB");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        ehClient = clientFactory.createEventHubClient(conn, scheduledExecutorService);
    }

    @Bean
    public EvtHubSender evtHubSender() {
        return msg -> {
            try {
                tpe.submit(() -> {
                    counters.evtAttemptCount().increment();
                    send(msg, 0);
                });
            } catch (RejectedExecutionException rejected) {
                counters.evtRejected().increment();
                LOG.error("too many tasks in queue, rejecting: {}", msg.hashCode());
            }
        };
    }

    private void send(String msg, int attemptCount) {
        byte[] payloadBytes = msg.getBytes(Charset.defaultCharset());
        EventData sendEvent = EventData.create(payloadBytes);
        if (attemptCount > 0) {
            LOG.info("retrying to send ({}): {}", attemptCount, msg.hashCode());
        }

        try {
            ehClient.sendSync(sendEvent);
            counters.evtSuccess().increment();
        } catch (EventHubException e) {
            counters.evtFailure().increment();
            LOG.error(e.getMessage());
            if (attemptCount < maxAttempts) {
                counters.evtRetried().increment();
                try {
                    Thread.sleep(throttlingDelay * (1 << attemptCount));
                    send(msg, attemptCount + 1);
                } catch (InterruptedException interrupted) {
                    LOG.error("interrupted while waiting for resend, aborting");
                    Thread.currentThread().interrupt();
                    counters.evtAborted().increment();
                }
            } else {
                LOG.error("too many retry attempts, aborting");
                counters.evtAborted().increment();
            }

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
