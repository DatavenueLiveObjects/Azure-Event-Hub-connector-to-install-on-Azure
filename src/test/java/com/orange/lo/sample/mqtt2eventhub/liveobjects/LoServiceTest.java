package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import com.orange.lo.sdk.fifomqtt.DataManagementFifoCallback;
import com.orange.lo.sdk.mqtt.exceptions.LoMqttException;
import net.jodah.failsafe.RetryPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoServiceTest {

    @Mock
    private DataManagementFifo dataManagementFifo;

    @Mock
    private LOApiClient loApiClient;

    private RetryPolicy<Void> retryPolicy;

    @BeforeEach
    void setUp() {
        when(loApiClient.getDataManagementFifo()).thenReturn(dataManagementFifo);
        retryPolicy = new RetryPolicy<Void>()
                .handleIf(e -> e instanceof LoMqttException)
                .withMaxAttempts(-1)
                .withBackoff(1, 32, ChronoUnit.MILLIS)
                .withMaxDuration(Duration.ofMillis(1000));
    }

    @Test
    void startShouldRetryOnFailure() {
        doThrow(new LoMqttException())
                .doNothing()
                .when(dataManagementFifo)
                .connectAndSubscribe();
        LoService loService = new LoService(loApiClient, retryPolicy);
        loService.start();

        verify(dataManagementFifo, times(2)).connectAndSubscribe();
    }

    @Test
    void stopShouldRetryOnFailure() {
        doThrow(new LoMqttException())
                .doNothing()
                .when(dataManagementFifo)
                .disconnect();
        LoService loService = new LoService(loApiClient, retryPolicy);
        loService.stop();

        verify(dataManagementFifo, times(2)).disconnect();
    }
}