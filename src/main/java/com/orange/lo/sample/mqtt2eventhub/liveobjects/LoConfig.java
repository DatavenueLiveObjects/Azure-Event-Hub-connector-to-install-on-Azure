package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sdk.LOApiClientParameters;
import com.orange.lo.sdk.fifomqtt.DataManagementFifoCallback;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@ConfigurationPropertiesScan
public class LoConfig {

    @Bean
    public LOApiClientParameters loApiClientParameters(LoProperties loProperties, DataManagementFifoCallback callback) {
        return LOApiClientParameters.builder()
                .apiKey(loProperties.getApiKey())
                .connectionTimeout(loProperties.getConnectionTimeout())
                .automaticReconnect(loProperties.getAutomaticReconnect())
                .hostname(loProperties.getHostname())
                .topics(Collections.singletonList(loProperties.getTopic()))
                .messageQos(loProperties.getMessageQos())
                .keepAliveIntervalSeconds(loProperties.getKeepAliveIntervalSeconds())
                .mqttPersistenceDataDir(loProperties.getMqttPersistenceDir())
                .dataManagementMqttCallback(callback)
                .build();
    }
}
