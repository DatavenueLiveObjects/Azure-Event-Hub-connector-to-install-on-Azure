/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sample.mqtt2eventhub.evthub.EvtHubSender;
import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
import com.orange.lo.sdk.fifomqtt.DataManagementFifoCallback;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Component;

@Component
public class LoMqttHandler implements DataManagementFifoCallback {
    private final EvtHubSender evtHubSender;
    private final Counter mqttEvtCounter;

    public LoMqttHandler(EvtHubSender evtHubSender, Counters counterProvider) {
        this.evtHubSender = evtHubSender;
        mqttEvtCounter = counterProvider.mqttEvents();
    }

    @Override
    public void onMessage(String message) {
        mqttEvtCounter.increment();
        evtHubSender.send(message);
    }
}
