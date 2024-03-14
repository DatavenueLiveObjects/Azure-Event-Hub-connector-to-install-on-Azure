/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.microsoft.azure.eventhubs.*;
import com.orange.lo.sample.mqtt2eventhub.liveobjects.LoProperties;
import com.orange.lo.sample.mqtt2eventhub.utils.ConnectorHealthActuatorEndpoint;
import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
import com.orange.lo.sdk.LOApiClient;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;

@Component
public class EventHubSender {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventHubClient eventHubClient;
    private final Counters counters;
    private final EventHubProperties eventHubProperties;
    private final LoProperties loProperties;
    private final ExecutorService executorService;
    private final LOApiClient loApiClient;
    private final ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;

    EventHubSender(EventHubClient eventHubClient, Counters counters, EventHubProperties eventHubProperties,
                   LoProperties loProperties, ExecutorService executorService, LOApiClient loApiClient,
                   ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint) {
        this.eventHubClient = eventHubClient;
        this.counters = counters;
        this.eventHubProperties = eventHubProperties;
        this.loProperties = loProperties;
        this.executorService = executorService;
        this.loApiClient = loApiClient;
        this.connectorHealthActuatorEndpoint = connectorHealthActuatorEndpoint;
    }

    public void send(int loMessageId, String msg) {

        byte[] payloadBytes = msg.getBytes(Charset.defaultCharset());
        EventData sendEvent = EventData.create(payloadBytes);

        Failsafe.with(
                new RetryPolicy<Void>()
                        .withMaxAttempts(eventHubProperties.getMaxSendAttempts())
                        .withBackoff(eventHubProperties.getThrottlingDelay().toMillis(), Duration.ofMinutes(1).toMillis(), ChronoUnit.MILLIS)
                        .onRetry(r -> {
                            LOG.debug("Problem while sending message to Event Hub because of: {}. Retrying...", r.getLastFailure().getMessage());
                            counters.getMesasageSentAttemptFailedCounter().increment();
                        })
                        .onSuccess(r -> {
                            LOG.debug("Message was sent to Event Hub");
                            counters.getMesasageSentCounter().increment();
                            loApiClient.getDataManagementFifo().sendAck(loMessageId, loProperties.getMessageQos());
                        })
                        .onFailure(r -> {
                            LOG.error("Cannot send message with id = {} to Event Hub because of {}", loMessageId, r.getFailure());
                            counters.getMesasageSentFailedCounter().increment();
                            loApiClient.getDataManagementFifo().sendAck(loMessageId, loProperties.getMessageQos());
                        })
        ).with(executorService).run(execution -> {
            counters.getMesasageSentAttemptCounter().increment();
            try {
                eventHubClient.sendSync(sendEvent);
                connectorHealthActuatorEndpoint.setCloudConnectionStatus(true);
            } catch (AuthorizationFailedException | IllegalEntityException e) {
                LOG.error("Problem with connection. Check Event Hub credentials. " + e.getMessage(), e);
                connectorHealthActuatorEndpoint.setCloudConnectionStatus(false);
                throw e;
            }
        });
    }


    @PostConstruct
    private void checkConnection() {
        EventData sendEvent = EventData.create(new byte[0]);
        try {
            eventHubClient.sendSync(sendEvent);
        } catch (AuthorizationFailedException | IllegalEntityException e) {
            LOG.error("Problem with connection. Check Event Hub credentials. " + e.getMessage(), e);
            connectorHealthActuatorEndpoint.setCloudConnectionStatus(false);
        } catch (EventHubException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}