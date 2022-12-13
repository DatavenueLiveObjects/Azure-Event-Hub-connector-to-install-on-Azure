/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.modify;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orange.lo.sample.mqtt2eventhub.ConnectorApplication;
import com.orange.lo.sample.mqtt2eventhub.evthub.EventHubProperties;
import com.orange.lo.sample.mqtt2eventhub.liveobjects.LoProperties;

@ExtendWith(MockitoExtension.class)
public class ModifyConfigurationServiceTest {

	private static final String LO_API_KEY = "abcd";
	private static final String LO_API_KEY_UPDATED = "dcba";
	
	private static final long CONNECTION_TIMEOUT = 5000;
	private static final long CONNECTION_TIMEOUT_UPDATED = 6000;
	
	@TempDir
	File tempDir;
	
	private File configurationFile;
	
	private ModifyConfigurationService modifyConfigurationService;
	
	@BeforeEach
    void setUp() throws IOException {
		LoProperties loProperties = new LoProperties(null, LO_API_KEY, null, null, null, null, null, null);
		
		EventHubProperties eventHubProperties = new EventHubProperties();
		eventHubProperties.setConnectionTimeout(CONNECTION_TIMEOUT);
		
		configurationFile = new File(tempDir, "application.yml");
		FileUtils.fileWrite(configurationFile, "lo:\n  api-key: " + LO_API_KEY + "\nazure:\n  evt-hub:\n    connection-timeout: " + CONNECTION_TIMEOUT);
		
		modifyConfigurationService = new ModifyConfigurationService(loProperties, eventHubProperties, configurationFile);
	}
	
	@Test
	public void shouldReadParameters() {
		//when
		ModifyConfigurationProperties properties = modifyConfigurationService.getProperties();
		
		//then
		Assertions.assertEquals(LO_API_KEY, properties.getLoApiKey());
		Assertions.assertEquals(CONNECTION_TIMEOUT, properties.getEvtHubConnectionTimeout());
	}
	
	@Test
	public void shouldUpdateParameters() throws IOException {
		//given
		MockedStatic<ConnectorApplication> connectorApplication = Mockito.mockStatic(ConnectorApplication.class);
		ModifyConfigurationProperties modifyConfigurationProperties = new ModifyConfigurationProperties();
		modifyConfigurationProperties.setEvtHubConnectionTimeout(CONNECTION_TIMEOUT_UPDATED);
		modifyConfigurationProperties.setLoApiKey(LO_API_KEY_UPDATED);
		
		//when
		modifyConfigurationService.modify(modifyConfigurationProperties);
		
		//then
		String configuratioFileContent = FileUtils.fileRead(configurationFile);
		
		connectorApplication.verify(ConnectorApplication::restart);
		Assertions.assertTrue(configuratioFileContent.contains(LO_API_KEY_UPDATED));
		Assertions.assertTrue(configuratioFileContent.contains(String.valueOf(CONNECTION_TIMEOUT_UPDATED)));
	}
}