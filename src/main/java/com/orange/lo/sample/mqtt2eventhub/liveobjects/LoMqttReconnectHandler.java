/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sample.mqtt2eventhub.utils.ConnectorHealthActuatorEndpoint;
import com.orange.lo.sdk.mqtt.DataManagementReconnectCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class LoMqttReconnectHandler implements DataManagementReconnectCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;

    public LoMqttReconnectHandler(ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint) {
        this.connectorHealthActuatorEndpoint = connectorHealthActuatorEndpoint;
    }

    @Override
    public void connectComplete(boolean b, String s) {
        LOG.info("Hahahaah moj handler #1 {}", connectorHealthActuatorEndpoint.isLoConnectionStatus());
        connectorHealthActuatorEndpoint.setLoConnectionStatus(true);
        LOG.info("Hahahaah moj handler #2 {}", connectorHealthActuatorEndpoint.isLoConnectionStatus());
    }

    @Override
    public void connectionLost(Throwable throwable) {
        connectorHealthActuatorEndpoint.setLoConnectionStatus(false);
    }
}