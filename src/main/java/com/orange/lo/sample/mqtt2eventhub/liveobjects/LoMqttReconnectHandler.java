/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sample.mqtt2eventhub.utils.ConnectorHealthActuatorEndpoint;
import com.orange.lo.sdk.mqtt.DataManagementReconnectCallback;
import org.springframework.stereotype.Component;

@Component
public class LoMqttReconnectHandler implements DataManagementReconnectCallback {

    private final ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;

    public LoMqttReconnectHandler(ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint) {
        this.connectorHealthActuatorEndpoint = connectorHealthActuatorEndpoint;
    }

    @Override
    public void connectComplete(boolean b, String s) {
        connectorHealthActuatorEndpoint.setLoConnectionStatus(true);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        connectorHealthActuatorEndpoint.setLoConnectionStatus(false);
    }
}