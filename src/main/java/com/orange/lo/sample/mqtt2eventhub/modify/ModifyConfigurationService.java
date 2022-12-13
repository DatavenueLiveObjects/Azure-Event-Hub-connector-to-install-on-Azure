package com.orange.lo.sample.mqtt2eventhub.modify;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.orange.lo.sample.mqtt2eventhub.ConnectorApplication;
import com.orange.lo.sample.mqtt2eventhub.evthub.EventHubProperties;
import com.orange.lo.sample.mqtt2eventhub.liveobjects.LoProperties;

@Component
public class ModifyConfigurationService {

    private LoProperties loProperties;
	private EventHubProperties eventHubProperties;
	private File configurationFile;

	public ModifyConfigurationService(LoProperties loProperties, EventHubProperties eventHubProperties, File configurationFile) {
		this.loProperties = loProperties;
		this.eventHubProperties = eventHubProperties;
		this.configurationFile = configurationFile;
    }

    public ModifyConfigurationProperties getProperties() {
    	ModifyConfigurationProperties modifyConfigurationProperties = new ModifyConfigurationProperties();
    	
    	modifyConfigurationProperties.setLoApiKey(loProperties.getApiKey());
    	modifyConfigurationProperties.setLoAutomaticReconnect(loProperties.getAutomaticReconnect());
    	modifyConfigurationProperties.setLoConnectionTimeout(loProperties.getConnectionTimeout());
    	modifyConfigurationProperties.setLoHostname(loProperties.getHostname());
    	modifyConfigurationProperties.setLoKeepAliveIntervalSeconds(loProperties.getKeepAliveIntervalSeconds());
    	modifyConfigurationProperties.setLoMessageQos(loProperties.getMessageQos());
    	modifyConfigurationProperties.setLoTopic(loProperties.getTopic());

    	modifyConfigurationProperties.setEvtHubConnectionTimeout(eventHubProperties.getConnectionTimeout());
    	modifyConfigurationProperties.setEvtHubMaxSendAttempts(eventHubProperties.getMaxSendAttempts());
    	modifyConfigurationProperties.setEvtHubName(eventHubProperties.getEvtHubName());
    	modifyConfigurationProperties.setEvtHubNameSpace(eventHubProperties.getNameSpace());
    	modifyConfigurationProperties.setEvtHubSasKey(eventHubProperties.getSasKey());
    	modifyConfigurationProperties.setEvtHubSasKeyName(eventHubProperties.getSasKeyName());
    	modifyConfigurationProperties.setEvtHubTaskQueueSize(eventHubProperties.getTaskQueueSize());
    	modifyConfigurationProperties.setEvtHubThreadPoolSize(eventHubProperties.getThreadPoolSize());
    	modifyConfigurationProperties.setEvtHubThrottlingDelay(eventHubProperties.getThrottlingDelay());
    	
        return modifyConfigurationProperties;
    }

    public void modify(ModifyConfigurationProperties modifyConfigurationProperties) {

		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			ObjectNode root = (ObjectNode) mapper.readTree(configurationFile);
			
			ObjectNode loNode = (ObjectNode) root.get("lo");
			
			setProperty(loNode, "hostname", () -> modifyConfigurationProperties.getLoHostname());
			setProperty(loNode, "api-key", () -> modifyConfigurationProperties.getLoApiKey());
			setProperty(loNode, "topic", () -> modifyConfigurationProperties.getLoTopic());
			setProperty(loNode, "keep-alive-interval-seconds",
					() -> modifyConfigurationProperties.getLoKeepAliveIntervalSeconds());
			setProperty(loNode, "automatic-reconnect",
					() -> modifyConfigurationProperties.getLoAutomaticReconnect());
			setProperty(loNode, "message-qos", () -> modifyConfigurationProperties.getLoMessageQos());
			setProperty(loNode, "connection-timeout", () -> modifyConfigurationProperties.getLoConnectionTimeout());
			
			ObjectNode evtHubNode = (ObjectNode) root.get("azure").get("evt-hub");
			
			setProperty(evtHubNode, "name-space", () -> modifyConfigurationProperties.getEvtHubNameSpace());
			setProperty(evtHubNode, "evt-hub-name", () -> modifyConfigurationProperties.getEvtHubName());
			setProperty(evtHubNode, "sas-key-name", () -> modifyConfigurationProperties.getEvtHubSasKeyName());
			setProperty(evtHubNode, "sas-key", () -> modifyConfigurationProperties.getEvtHubSasKey());
			setProperty(evtHubNode, "thread-pool-size", () -> modifyConfigurationProperties.getEvtHubThreadPoolSize());
			setProperty(evtHubNode, "task-queue-size", () -> modifyConfigurationProperties.getEvtHubTaskQueueSize());
			setProperty(evtHubNode, "connection-timeout", () -> modifyConfigurationProperties.getEvtHubConnectionTimeout());
			setProperty(evtHubNode, "throttling-delay", () -> modifyConfigurationProperties.getEvtHubThrottlingDelay());
			setProperty(evtHubNode, "max-send-attempts", () -> modifyConfigurationProperties.getEvtHubMaxSendAttempts());
				
			mapper.writer().writeValue(configurationFile, root);
			ConnectorApplication.restart();
		} catch (IOException e) {
			throw new ModifyException("Error while modifying configuration", e);
		}
    }
    
    private void setProperty(ObjectNode node, String parameterName, Supplier<Object> parameterSupplier) {

		Object parameter = parameterSupplier.get();
		if (Objects.isNull(parameter)) {
			return;
		}

		if (parameter instanceof Integer) {
			node.put(parameterName, (Integer) parameter);
		} else if (parameter instanceof Long) {
			node.put(parameterName, (Long) parameter);
		} else if (parameter instanceof Boolean) {
			node.put(parameterName, (Boolean) parameter);
		} else {
			node.put(parameterName, String.valueOf(parameter));
		}
	}
}