/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoServiceTest {

    @Mock
    private DataManagementFifo dataManagementFifo;
    @Mock
    private LOApiClient loApiClient;
    @Mock
    private LoProperties loProperties;
    @Mock
    private Counters counters;

    @BeforeEach
    void setUp() {
        when(loApiClient.getDataManagementFifo()).thenReturn(dataManagementFifo);
    }

    @Test
    void startShouldConnectAndSubscribe() {
        when(counters.isLoConnectionStatusUp()).thenReturn(true);
        when(counters.isCloudConnectionStatusUp()).thenReturn(true);

        LoService loService = new LoService(loApiClient, counters, loProperties);
        loService.start();

        verify(dataManagementFifo, times(1)).connectAndSubscribe();
    }

    @Test
    void shouldInvokeDisconnect() {
        LoService loService = new LoService(loApiClient, counters, loProperties);
        loService.stop();

        verify(dataManagementFifo, times(1)).disconnect();
    }
    @Test
    void shouldSendAck() {
        LoService loService = new LoService(loApiClient, counters, loProperties);
        int messageId = 12345;
        loService.sendAck(messageId);

        verify(dataManagementFifo, times(1)).sendAck(messageId, 0);
    }
}