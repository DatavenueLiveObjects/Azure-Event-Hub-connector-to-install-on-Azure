/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sample.mqtt2eventhub.utils.ConnectorHealthActuatorEndpoint;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import com.orange.lo.sdk.mqtt.exceptions.LoMqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.invoke.MethodHandles;

@Component
public class LoService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DataManagementFifo dataManagementFifo;

    private final ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;

    public LoService(LOApiClient loApiClient, ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint) {
        this.dataManagementFifo = loApiClient.getDataManagementFifo();
        this.connectorHealthActuatorEndpoint = connectorHealthActuatorEndpoint;
    }

    @PostConstruct
    public void start() {
        try {
            dataManagementFifo.connect();
        } catch (LoMqttException e) {
            LOG.error("Problem with connection. Check LO credentials. ", e);
            connectorHealthActuatorEndpoint.setLoConnectionStatus(false);
        }

        if (connectorHealthActuatorEndpoint.isCloudConnectionStatus() && connectorHealthActuatorEndpoint.isLoConnectionStatus())
            dataManagementFifo.connectAndSubscribe();
    }

    @PreDestroy
    public void stop() {
        dataManagementFifo.disconnect();
    }
}
