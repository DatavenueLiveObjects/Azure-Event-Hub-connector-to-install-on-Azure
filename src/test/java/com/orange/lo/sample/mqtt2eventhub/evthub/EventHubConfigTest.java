package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventHubConfigTest {

    @Mock
    private Counters counters;
    @Mock
    private EventHubClient eventHubClient;
    private EventHubConfig eventHubConfig;

    @BeforeEach
    void setUp() throws IOException, EventHubException {
        EventHubProperties eventHubProperties = new EventHubProperties();
        eventHubProperties.setThreadPoolSize(20);
        eventHubProperties.setTaskQueueSize(20);
        eventHubProperties.setMaxSendAttempts(1);
        eventHubProperties.setThrottlingDelay(Duration.ofMillis(1));
        eventHubConfig = new EventHubConfig(eventHubProperties, counters, (conn, executor) -> eventHubClient);
    }

    @Test
    void shouldCreateEvtHubSender() {
        EvtHubSender evtHubSender = eventHubConfig.evtHubSender();

        assertNotNull(evtHubSender);
    }

    @Test
    void shouldCallEventHubClientCloseOnDestroy() throws EventHubException {
        eventHubConfig.onDestroy();

        verify(eventHubClient, times(1)).closeSync();
    }

}