package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sample.mqtt2eventhub.evthub.EvtHubSender;
import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
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
    private EvtHubSender evtHubSender;
    @Mock
    private Counters counters;
    @Mock
    private Counter counter;
    private LoMqttHandler loMqttHandler;

    @BeforeEach
    void setUp() {
        when(counters.mqttEvents()).thenReturn(counter);
        loMqttHandler = new LoMqttHandler(evtHubSender, counters);
    }

    @Test
    void shouldCallEvtHubSenderAndCounterWhenMessageIsHandled() {
        Message<String> message = new GenericMessage<>("{}");

        loMqttHandler.onMessage(message.getPayload());

        verify(counter, times(1)).increment();
        verify(evtHubSender, times(1)).send(message.getPayload());
    }
}