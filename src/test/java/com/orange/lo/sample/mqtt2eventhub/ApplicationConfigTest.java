package com.orange.lo.sample.mqtt2eventhub;

import com.orange.lo.sample.mqtt2eventhub.liveobjects.LoMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationConfigTest {

    private ApplicationConfig applicationConfig;

    @BeforeEach
    void setUp() {
        applicationConfig = new ApplicationConfig();
    }

    @Test
    void messageQueueShouldBeThreadSafe() {
        Queue<LoMessage> loMessages = applicationConfig.messageQueue();

        assertInstanceOf(ConcurrentLinkedQueue.class, loMessages);
    }
}