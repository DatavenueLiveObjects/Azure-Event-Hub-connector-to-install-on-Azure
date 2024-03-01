/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import com.orange.lo.sdk.mqtt.exceptions.LoMqttException;
import net.jodah.failsafe.RetryPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

    @BeforeEach
    void setUp() {
        when(loApiClient.getDataManagementFifo()).thenReturn(dataManagementFifo);
    }

    @Test
    void startShouldConnectAndSubscribe() {

        LoService loService = new LoService(loApiClient);
        loService.start();

        verify(dataManagementFifo, times(1)).connectAndSubscribe();
    }

    @Test
    void shouldInvokeDisconnect() {
        LoService loService = new LoService(loApiClient);
        loService.stop();

        verify(dataManagementFifo, times(1)).disconnect();
    }
}