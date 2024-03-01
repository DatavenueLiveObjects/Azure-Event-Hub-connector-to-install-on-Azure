/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.LOApiClientParameters;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Collections;

@Configuration
@ConfigurationPropertiesScan
public class LoConfig {

	private final LoProperties loProperties;
	private final LoMqttHandler loMqttHandler;

	public LoConfig(LoProperties loProperties, LoMqttHandler loMqttHandler) {
		this.loProperties = loProperties;
		this.loMqttHandler = loMqttHandler;
	}

	@Bean
	public LOApiClient loApiClient() {
		return new LOApiClient(loApiClientParameters());
	}

    private LOApiClientParameters loApiClientParameters() {
        return LOApiClientParameters.builder()
                .apiKey(loProperties.getApiKey())
                .connectionTimeout(loProperties.getConnectionTimeout())
                .automaticReconnect(loProperties.getAutomaticReconnect())
                .hostname(loProperties.getHostname())
                .topics(Collections.singletonList(loProperties.getTopic()))
				.manualAck(true)
                .messageQos(loProperties.getMessageQos())
                .keepAliveIntervalSeconds(loProperties.getKeepAliveIntervalSeconds())
                .mqttPersistenceDataDir(loProperties.getMqttPersistenceDir())
                .dataManagementMqttCallback(loMqttHandler)
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
