package com.orange.lo.sample.mqtt2eventhub.utils;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectorHealthActuatorEndpointTest {

    private ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;


    @BeforeEach
    void setUp() {
        DataManagementFifo dataManagementFifo = Mockito.mock(DataManagementFifo.class);
        LOApiClient loApiClient = Mockito.mock(LOApiClient.class);
        Mockito.when(loApiClient.getDataManagementFifo()).thenReturn(dataManagementFifo);
        this.connectorHealthActuatorEndpoint = new ConnectorHealthActuatorEndpoint(loApiClient);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void checkCloudConnectionStatus(boolean isConnected) {
        // when
        connectorHealthActuatorEndpoint.setCloudConnectionStatus(isConnected);
        boolean cloudConnectionStatus = (boolean) connectorHealthActuatorEndpoint.health().getDetails()
                .get("cloudConnectionStatus");

        // then
        assertEquals(cloudConnectionStatus, isConnected);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(Arguments.of(true),
                Arguments.of(false));
    }

}