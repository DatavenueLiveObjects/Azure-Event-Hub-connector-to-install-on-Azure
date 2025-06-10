package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifoCallback;
import com.orange.lo.sdk.mqtt.DataManagementReconnectCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class})
@EnableConfigurationProperties(LoProperties.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("unittest")
class LoConfigTest {

    private DataManagementFifoCallback dataManagementFifoCallback = (i, s) -> {

    };
    private final DataManagementReconnectCallback dataManagementReconnectCallback = new DataManagementReconnectCallback() {

        @Override
        public void connectComplete(boolean b, String s) {

        }

        @Override
        public void connectionLost(Throwable throwable) {

        }
    };

    @Autowired
    private LoProperties loProperties;
    private LoConfig loConfig;

    @BeforeEach
    void setUp() {
        loConfig = new LoConfig(loProperties, dataManagementFifoCallback, dataManagementReconnectCallback);
    }

    @Test
    void shouldInitLoApiClient() {
        LOApiClient loApiClient = loConfig.loApiClient();

        assertNotNull(loApiClient);
    }
}