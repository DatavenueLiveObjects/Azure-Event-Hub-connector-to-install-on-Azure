/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.orange.lo.sample.mqtt2eventhub.utils.Counters;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventHubConfigTest {

    @Mock
    private EventHubClient eventHubClient;
    @Mock()
    private ThreadPoolExecutor threadPoolExecutor;

    @Mock
    private Counters counters;
    
    @Mock
    private Counter mesasageSentAttemptCounter;
    
    private EventHubConfig eventHubConfig;

    @BeforeEach
    void setUp() throws IOException, EventHubException {
        EventHubProperties eventHubProperties = new EventHubProperties();
        eventHubProperties.setThreadPoolSize(20);
        eventHubProperties.setTaskQueueSize(20);
        eventHubProperties.setMaxSendAttempts(2);
        eventHubProperties.setThrottlingDelay(Duration.ofMillis(1));
        
        
        
        
        eventHubConfig = new EventHubConfig(eventHubProperties, counters, (conn, executor) -> eventHubClient, threadPoolExecutor);
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

    @Test
    void shouldRetryOnEventHubException() throws EventHubException {
    	when(counters.getMesasageSentAttemptCounter()).thenReturn(mesasageSentAttemptCounter);
        doThrow(new EventHubException(false, "dummy"))
                .doNothing()
                .when(eventHubClient)
                .sendSync(Mockito.any(EventData.class));
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        })
                .when(threadPoolExecutor)
                .submit(Mockito.any(Runnable.class));
        EvtHubSender sender = eventHubConfig.evtHubSender();

        sender.send("");

        verify(eventHubClient, times(2)).sendSync(Mockito.any(EventData.class));
    }

    @Test
    void shouldAbortOnRuntimeException() throws EventHubException {
    	when(counters.getMesasageSentAttemptCounter()).thenReturn(mesasageSentAttemptCounter);
        doThrow(new RuntimeException())
                .doNothing()
                .when(eventHubClient)
                .sendSync(Mockito.any(EventData.class));
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        })
                .when(threadPoolExecutor)
                .submit(Mockito.any(Runnable.class));
        EvtHubSender sender = eventHubConfig.evtHubSender();

        sender.send("");

        verify(eventHubClient, times(1)).sendSync(Mockito.any(EventData.class));
    }
}