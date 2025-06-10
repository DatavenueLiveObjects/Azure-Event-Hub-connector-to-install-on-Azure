package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import org.junit.jupiter.api.Assertions;
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
class LoPropertiesTest {

    @Autowired
    private LoProperties loProperties;

    @Test
    void shouldBindLoPropertiesFromYamlConfiguration() {
        Assertions.assertAll(
                () -> assertEquals("invalid.url.liveobjects.orange-business.com", this.loProperties.getHostname()),
                () -> assertEquals("test", this.loProperties.getApiKey()),
                () -> assertEquals("dev", this.loProperties.getTopic()),
                () -> assertEquals(30, this.loProperties.getKeepAliveIntervalSeconds()),
                () -> assertEquals(true, this.loProperties.getAutomaticReconnect()),
                () -> assertEquals(1, this.loProperties.getMessageQos()),
                () -> assertEquals("./temp/", this.loProperties.getMqttPersistenceDir()),
                () -> assertEquals(30000, this.loProperties.getConnectionTimeout())
        );
    }
}