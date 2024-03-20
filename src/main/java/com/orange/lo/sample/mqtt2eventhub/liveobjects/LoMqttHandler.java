/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sample.mqtt2eventhub.evthub.EventHubSender;
import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifoCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class LoMqttHandler implements DataManagementFifoCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventHubSender eventHubSender;
    private final Counters counters;
    private final LOApiClient loApiClient;
    private final LoProperties loProperties;

    public LoMqttHandler(@Lazy EventHubSender eventHubSender, @Lazy LOApiClient loApiClient, Counters counters, LoProperties loProperties) {
        this.eventHubSender = eventHubSender;
        this.counters = counters;
        this.loApiClient = loApiClient;
        this.loProperties = loProperties;
    }

    @Override
    public void onMessage(int loMessageId, String message) {
        counters.getMesasageReadCounter().increment();
        try {
            eventHubSender.send(loMessageId, message);
        } catch (Exception e) {
            LOG.error("Cannot send message with id = {} to Event Hub because of {}", loMessageId, e);
            counters.getMesasageSentFailedCounter().increment();
            loApiClient.getDataManagementFifo().sendAck(loMessageId, loProperties.getMessageQos());
        }
    }
}