package com.orange.lo.sample.mqtt2eventhub.evthub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventHubPropertiesTest {

    private EventHubProperties eventHubProperties;

    @BeforeEach
    void setUp() {
        eventHubProperties = new EventHubProperties();
    }

    @Test
    void shouldSetNameSpace() {
        String nameSpace = "name-space";
        eventHubProperties.setNameSpace(nameSpace);

        assertEquals(nameSpace, eventHubProperties.getNameSpace());
    }

    @Test
    void shouldSetEvtHubName() {
        String evtHubName = "evt-hub-name";
        eventHubProperties.setEvtHubName(evtHubName);

        assertEquals(evtHubName, eventHubProperties.getEvtHubName());
    }

    @Test
    void shouldSetSasKeyName() {
        String sasKeyName = "sas-key-name";
        eventHubProperties.setSasKeyName(sasKeyName);

        assertEquals(sasKeyName, eventHubProperties.getSasKeyName());
    }

    @Test
    void shouldSetSasKey() {
        String sasKey = "aBcDEF132";
        eventHubProperties.setSasKey(sasKey);

        assertEquals(sasKey, eventHubProperties.getSasKey());
    }

    @Test
    void shouldSetThreadPoolSize() {
        int threadPoolSize = 20;
        eventHubProperties.setThreadPoolSize(threadPoolSize);

        assertEquals(threadPoolSize, eventHubProperties.getThreadPoolSize());
    }

    @Test
    void shouldSetConnectionTimeout() {
        int connectionTimeout = 3000;
        eventHubProperties.setConnectionTimeout(connectionTimeout);

        assertEquals(connectionTimeout, eventHubProperties.getConnectionTimeout());
    }

    @Test
    void shouldSetTaskQueueSize() {
        int taskQueueSize = 30;
        eventHubProperties.setTaskQueueSize(taskQueueSize);

        assertEquals(taskQueueSize, eventHubProperties.getTaskQueueSize());
    }

    @Test
    void shouldSetThrottlingDelay() {
        int throttlingDelay = 500;
        eventHubProperties.setThrottlingDelay(throttlingDelay);

        assertEquals(throttlingDelay, eventHubProperties.getThrottlingDelay());
    }

    @Test
    void shouldSetMaxSendAttempts() {
        int maxSendAttempts = 5;
        eventHubProperties.setMaxSendAttempts(maxSendAttempts);

        assertEquals(maxSendAttempts, eventHubProperties.getMaxSendAttempts());
    }
}