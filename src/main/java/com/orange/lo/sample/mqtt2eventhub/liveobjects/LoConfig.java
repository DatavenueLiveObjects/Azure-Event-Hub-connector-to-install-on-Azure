package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Collections;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.orange.lo.sdk.LOApiClientParameters;
import com.orange.lo.sdk.fifomqtt.DataManagementFifoCallback;

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
                .connectorType(loProperties.getConnectorType())
                .connectorVersion(getConnectorVersion())
                .build();
    }
    
    private String getConnectorVersion() {
    	MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {			
	        if ((new File("pom.xml")).exists()) {
	          model = reader.read(new FileReader("pom.xml"));
	        } else {
	          model = reader.read(
	            new InputStreamReader(
	            	LoConfig.class.getResourceAsStream(
	                "/META-INF/maven/com.orange.lo.sample/mqtt2eventhub/pom.xml"
	              )
	            )
	          );
	        }
	        return model.getVersion().replace(".", "_");
        } catch (Exception e) {
			return "";
		}
    }
}
