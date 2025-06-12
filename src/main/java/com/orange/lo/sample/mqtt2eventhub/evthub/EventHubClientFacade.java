/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class EventHubClientFacade {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String EVENT_HUB_CLIENT_NOT_INITIALIZED_MESSAGE = "The event hub client has not been initialized. Check the validity of your credentials";

    private final Counters counters;
    private final EventHubProperties eventHubProperties;
    private EventHubClient eventHubClient;

    public EventHubClientFacade(EventHubProperties eventHubProperties, Counters counters) {
        this.eventHubProperties = eventHubProperties;
        this.counters = counters;
    }

    @PostConstruct
    private void init() {
        ConnectionStringBuilder conn = new ConnectionStringBuilder()
                .setOperationTimeout(Duration.of(eventHubProperties.getConnectionTimeout(), ChronoUnit.MILLIS))
                .setNamespaceName(eventHubProperties.getNameSpace())
                .setEventHubName(eventHubProperties.getEvtHubName())
                .setSasKeyName(eventHubProperties.getSasKeyName())
                .setSasKey(eventHubProperties.getSasKey());

        this.eventHubClient = null;
        try {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(eventHubProperties.getThreadPoolSize());
            this.eventHubClient = EventHubClient.createFromConnectionStringSync(conn.toString(), executor);
        } catch (Exception e) {
            LOG.error("Problem with connection. Check Event Hub credentials. " + e.getMessage(), e);
            counters.setCloudConnectionStatus(false);
        }
    }

    public void sendSync(List<String> messages) throws EventHubClientFacadeException {
        List<EventData> list = messages.stream().map(msg -> {
            byte[] bytes = msg.getBytes(Charset.defaultCharset());
            return EventData.create(bytes);
        }).toList();
        sendSync(list);
    }

    public void sendSync(String msg) throws EventHubClientFacadeException {
        byte[] payloadBytes = msg.getBytes(Charset.defaultCharset());
        sendSync(payloadBytes);
    }

    public void sendSync(byte[] payloadBytes) throws EventHubClientFacadeException {
        EventData sendEvent = EventData.create(payloadBytes);
        List<EventData> list = List.of(sendEvent);
        sendSync(list);
    }

    public void sendSync(Iterable<EventData> eventDatas) throws EventHubClientFacadeException {
        try {
            eventHubClient.sendSync(eventDatas);
        } catch (NullPointerException e) {
            throw new EventHubClientFacadeException(EVENT_HUB_CLIENT_NOT_INITIALIZED_MESSAGE);
        } catch (EventHubException e) {
            throw new EventHubClientFacadeException(e);
        }
    }

    @PreDestroy
    public void onDestroy() {
        try {
            LOG.info("shutting down eventHubClient");
            eventHubClient.closeSync();
        } catch (EventHubException e) {
            LOG.error(e.getMessage());
        }
    }
}
