package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "lo")
@ConstructorBinding
public class LoProperties {

    private final String hostname;
    private final String apiKey;
    private final String topic;
    private final Integer messageQos;
    private final String mqttPersistenceDir;
    private final Integer keepAliveIntervalSeconds;
    private final Integer connectionTimeout;
    private final Boolean automaticReconnect;

    public LoProperties(
            String hostname,
            String apiKey,
            String topic,
            Integer messageQos,
            String mqttPersistenceDir,
            Integer keepAliveIntervalSeconds,
            Integer connectionTimeout,
            Boolean automaticReconnect) {
        this.hostname = hostname;
        this.apiKey = apiKey;
        this.topic = topic;
        this.messageQos = messageQos;
        this.mqttPersistenceDir = mqttPersistenceDir;
        this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
        this.connectionTimeout = connectionTimeout;
        this.automaticReconnect = automaticReconnect;
    }

    public String getHostname() {
        return hostname;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getMessageQos() {
        return messageQos;
    }

    public String getMqttPersistenceDir() {
        return mqttPersistenceDir;
    }

    public Integer getKeepAliveIntervalSeconds() {
        return keepAliveIntervalSeconds;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Boolean getAutomaticReconnect() {
        return automaticReconnect;
    }
}