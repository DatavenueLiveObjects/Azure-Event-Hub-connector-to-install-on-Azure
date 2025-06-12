/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.orange.lo.sample.mqtt2eventhub.liveobjects.LoMessage;
import com.orange.lo.sample.mqtt2eventhub.liveobjects.LoService;
import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

@Component
@EnableScheduling
public class EventHubSender {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int DEFAULT_BATCH_SIZE = 10;

    private final EventHubClientFacade eventHubClientFacade;
    private final Counters counters;
    private final EventHubProperties eventHubProperties;
    private final ExecutorService executorService;
    private final LoService loService;
    private final Queue<LoMessage> messageQueue;

    EventHubSender(EventHubClientFacade eventHubClientFacade, Counters counters, EventHubProperties eventHubProperties,
                   ExecutorService executorService, LoService loService, Queue<LoMessage> messageQueue) {
        this.eventHubClientFacade = eventHubClientFacade;
        this.counters = counters;
        this.eventHubProperties = eventHubProperties;
        this.executorService = executorService;
        this.loService = loService;
        this.messageQueue = messageQueue;
    }

    public void send(List<LoMessage> messages) {
        Failsafe.with(
                new RetryPolicy<Void>()
                        .withMaxAttempts(eventHubProperties.getMaxSendAttempts())
                        .withBackoff(eventHubProperties.getThrottlingDelay().toMillis(), Duration.ofMinutes(1).toMillis(), ChronoUnit.MILLIS)
                        .onRetry(r -> {
                            LOG.debug("Problem while sending message to Event Hub because of: {}. Retrying...", r.getLastFailure().getMessage());
                            counters.getMesasageSentAttemptFailedCounter().increment(messages.size());
                        })
                        .onSuccess(r -> {
                            LOG.debug("Batch of messages of the following size were sent: {}", messages.size());
                            counters.getMesasageSentCounter().increment(messages.size());
                            messages.forEach(m -> loService.sendAck(m.messageId()));
                        })
                        .onFailure(r -> {
                            LOG.error("Cannot send messages to Event Hub because of {}", r.getFailure());
                            counters.getMesasageSentFailedCounter().increment(messages.size());
                            messages.forEach(m -> loService.sendAck(m.messageId()));
                        })
        ).with(executorService).run(execution -> {
            counters.getMesasageSentAttemptCounter().increment(messages.size());
            try {
                List<String> messageContentList = messages.stream().map(LoMessage::message).toList();
                eventHubClientFacade.sendSync(messageContentList);
                counters.setCloudConnectionStatus(true);
            } catch (EventHubClientFacadeException e) {
                LOG.error("Problem with connection. Check Event Hub credentials. " + e.getMessage(), e);
                counters.setCloudConnectionStatus(false);
                throw e;
            }
        });
    }

    @PostConstruct
    private void checkConnection() {
        try {
            eventHubClientFacade.sendSync(new byte[0]);
        } catch (EventHubClientFacadeException e) {
            LOG.error("Problem with connection. Check Event Hub credentials. " + e.getMessage(), e);
            counters.setCloudConnectionStatus(false);
        }
    }

    @Scheduled(fixedRateString = "${azure.evt-hub.synchronization-interval}")
    public void send() {
        LOG.debug("Number of messages waiting to be sent: {}", messageQueue.size());
        if (!messageQueue.isEmpty()) {
            LOG.info("Start sending messages...");

            int batchSize = eventHubProperties.getMessageBatchSize() != null
                    ? eventHubProperties.getMessageBatchSize() : DEFAULT_BATCH_SIZE;

            List<LoMessage> messageBatch = new ArrayList<>(batchSize);
            while (!messageQueue.isEmpty()) {
                messageBatch.add(messageQueue.poll());
                if (messageBatch.size() == batchSize) {
                    send(new ArrayList<>(messageBatch));
                    messageBatch.clear();
                }
            }
            if (!messageBatch.isEmpty()) {
                send(new ArrayList<>(messageBatch));
            }
        }
    }

}
