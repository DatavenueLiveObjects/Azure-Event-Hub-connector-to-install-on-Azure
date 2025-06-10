package com.orange.lo.sample.mqtt2eventhub.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.actuate.health.Health;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectorHealthActuatorEndpointTest {

    private ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;


    @BeforeEach
    void setUp() {
        this.connectorHealthActuatorEndpoint = new ConnectorHealthActuatorEndpoint();
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void checkCloudConnectionStatus(boolean isConnected) {
        // when
        connectorHealthActuatorEndpoint.setCloudConnectionStatus(isConnected);
        boolean cloudConnectionStatus = (boolean) connectorHealthActuatorEndpoint.health().getDetails()
                .get("cloudConnectionStatus");

        // then
        assertEquals(isConnected, cloudConnectionStatus);
        assertEquals(isConnected, connectorHealthActuatorEndpoint.isCloudConnectionStatus());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void checkLoConnectionStatus(boolean isConnected) {
        // when
        connectorHealthActuatorEndpoint.setLoConnectionStatus(isConnected);
        boolean loMqttConnectionStatus = (boolean) connectorHealthActuatorEndpoint.health().getDetails()
                .get("loMqttConnectionStatus");

        // then
        assertEquals(isConnected, loMqttConnectionStatus);
        assertEquals(isConnected, connectorHealthActuatorEndpoint.isLoConnectionStatus());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void checkHealth(boolean includeDetails) {
        // when
        Health health = connectorHealthActuatorEndpoint.getHealth(includeDetails);
        Map<String, Object> details = health.getDetails();
        int expectedDetailsSize = includeDetails ? 2 : 0;

        // then
        assertEquals(expectedDetailsSize, details.size());
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(Arguments.of(true),
                Arguments.of(false));
    }

}