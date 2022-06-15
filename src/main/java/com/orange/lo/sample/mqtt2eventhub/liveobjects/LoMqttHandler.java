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
    private final Counter mesasageReadCounter;

    public LoMqttHandler(EvtHubSender evtHubSender, Counters counters) {
        this.evtHubSender = evtHubSender;
        mesasageReadCounter = counters.getMesasageReadCounter();
    }

    @Override
    public void onMessage(String message) {
    	mesasageReadCounter.increment();
        evtHubSender.send(message);
    }
}
