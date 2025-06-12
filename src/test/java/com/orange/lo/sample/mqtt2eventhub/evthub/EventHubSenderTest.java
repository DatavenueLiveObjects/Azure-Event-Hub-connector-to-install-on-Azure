package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.orange.lo.sample.mqtt2eventhub.liveobjects.LoMessage;
import com.orange.lo.sample.mqtt2eventhub.liveobjects.LoService;
import com.orange.lo.sample.mqtt2eventhub.utils.ConnectorHealthActuatorEndpoint;
import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventHubSenderTest {
    @Mock
    private EventHubClientFacade eventHubClientFacade;
    @Mock
    private Counters counters;
    @Mock
    private Counter counter;
    @Mock
    private EventHubProperties eventHubProperties;
    @Mock
    private ExecutorService executorService;
    @Mock
    private ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;
    @Mock
    private LoService loService;

    EventHubSender eventHubSender;

    @BeforeEach
    void setUp() {
        prepareService(new LinkedList<>());
    }

    private void prepareService(LinkedList<LoMessage> messageQueue) {
        eventHubSender = new EventHubSender(eventHubClientFacade, counters, eventHubProperties, executorService,
                connectorHealthActuatorEndpoint, loService, messageQueue);
    }

    @Test
    void shouldNotSendMessagesIfQueueIsEmpty() throws EventHubClientFacadeException {
        // when
        eventHubSender.send();

        // then
        verify(eventHubClientFacade, never()).sendSync(ArgumentMatchers.<String>anyList());
    }

    @Test
    void shouldSendMessagesInOneBatchIfQueueNotExceedMessageBatchSizeProperty() throws EventHubClientFacadeException {
        // given
        int batchSize = 5;

        when(eventHubProperties.getMessageBatchSize()).thenReturn(batchSize);
        when(eventHubProperties.getMaxSendAttempts()).thenReturn(10);
        when(eventHubProperties.getThrottlingDelay()).thenReturn(Duration.ofMillis(1));
        when(counters.getMesasageSentAttemptCounter()).thenReturn(counter);

        LinkedList<LoMessage> messageQueue = getExampleMessageQueue(batchSize);

        prepareService(messageQueue);

        List<String> expectedMessages = messageQueue.stream().map(LoMessage::message).toList();

        // when
        eventHubSender.send();

        // then
        verify(eventHubClientFacade, times(1)).sendSync(expectedMessages);
    }

    @Test
    void shouldSplitMessagesIntoPacketsIfQueueExceedMessageBatchSizeProperty() throws EventHubClientFacadeException {
        // given
        int batchSize = 5;
        int totalLength = batchSize + 1;

        when(eventHubProperties.getMessageBatchSize()).thenReturn(batchSize);
        when(eventHubProperties.getMaxSendAttempts()).thenReturn(10);
        when(eventHubProperties.getThrottlingDelay()).thenReturn(Duration.ofMillis(1));
        when(counters.getMesasageSentAttemptCounter()).thenReturn(counter);

        LinkedList<LoMessage> messageQueue = getExampleMessageQueue(totalLength);

        prepareService(messageQueue);

        List<String> expectedMessages1 = (new LinkedList<>(messageQueue)).subList(0, batchSize)
                .stream()
                .map(LoMessage::message)
                .toList();
        List<String> expectedMessages2 = (new LinkedList<>(messageQueue)).subList(batchSize, totalLength)
                .stream()
                .map(LoMessage::message)
                .toList();

        // when
        eventHubSender.send();

        // then
        verify(eventHubClientFacade, times(1)).sendSync(expectedMessages1);
        verify(eventHubClientFacade, times(1)).sendSync(expectedMessages2);
    }

    private LinkedList<LoMessage> getExampleMessageQueue(int batchSize) {
        return IntStream.range(1, batchSize + 1)
                .mapToObj(i -> new LoMessage(i, String.format("Message %d", i)))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    class IsListWithGivenSize implements ArgumentMatcher<List> {
        int size;

        public IsListWithGivenSize(int size) {
            this.size = size;
        }

        @Override
        public boolean matches(List list) {
            return list.size() == size;
        }
    }
}