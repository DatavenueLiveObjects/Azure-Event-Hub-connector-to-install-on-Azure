package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sample.mqtt2eventhub.utils.ConnectorHealthActuatorEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoMqttReconnectHandlerTest {

    @Mock
    private ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;
    private LoMqttReconnectHandler loMqttReconnectHandler;

    @BeforeEach
    void setUp() {
        this.loMqttReconnectHandler = new LoMqttReconnectHandler(connectorHealthActuatorEndpoint);
    }

    @Test
    void shouldChangeLoConnectionStausWhenConnectComplete() {
        loMqttReconnectHandler.connectComplete(false, "");

        verify(connectorHealthActuatorEndpoint, times(1)).setLoConnectionStatus(true);
    }

    @Test
    void shouldChangeLoConnectionStausWhenConnectionLost() {
        loMqttReconnectHandler.connectionLost(new Exception());

        verify(connectorHealthActuatorEndpoint, times(1)).setLoConnectionStatus(false);
    }
}