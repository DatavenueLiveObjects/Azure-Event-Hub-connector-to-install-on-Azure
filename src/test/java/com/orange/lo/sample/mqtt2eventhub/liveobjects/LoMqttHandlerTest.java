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

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoMqttHandlerTest {

    @Mock
    private Counters counters;
    @Mock
    private Counter receivedCounter;
    private Queue<LoMessage> messageQueue;
    private LoMqttHandler handler;

    @BeforeEach
    void setUp() {
        when(counters.getMesasageReadCounter()).thenReturn(receivedCounter);
        messageQueue = new LinkedList<>();
        handler = new LoMqttHandler(counters, messageQueue);
    }

    @Test
    public void shouldIncrementCounterOnMessage() {
        // when
        handler.onMessage(1, "test message");

        // then
        verify(receivedCounter, times(1)).increment();
    }

    @Test
    public void shouldAddMessageToQueueOnMessage() {
        // when
        handler.onMessage(1, "test message");

        // then
        assertEquals(1, messageQueue.size());
        assertEquals(new LoMessage(1, "test message"), messageQueue.peek());
    }
}