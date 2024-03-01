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
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoMqttHandlerTest {

    @Mock
    private EventHubSender eventHubSender;
    @Mock
    private LOApiClient loApiClient;
    @Mock
    private LoProperties loProperties;
    @Mock
    private Counters counters;
    @Mock
    private Counter counter;
    private LoMqttHandler loMqttHandler;

    @BeforeEach
    void setUp() {
        when(counters.getMesasageReadCounter()).thenReturn(counter);
        loMqttHandler = new LoMqttHandler(eventHubSender, loApiClient, counters, loProperties);
    }

    @Test
    void shouldCallEvtHubSenderAndCounterWhenMessageIsHandled() {
        String message = "{}";
        int loMessageId = 1;

        loMqttHandler.onMessage(loMessageId, message);

        verify(counter, times(1)).increment();
        verify(eventHubSender, times(1)).send(loMessageId, message);
    }
}