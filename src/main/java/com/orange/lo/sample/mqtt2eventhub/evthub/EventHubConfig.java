/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.orange.lo.sample.mqtt2eventhub.utils.ConnectorHealthActuatorEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class EventHubConfig {

    private final EventHubProperties eventHubProperties;

    private final ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;

    public EventHubConfig(EventHubProperties eventHubProperties, ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint) {
        this.eventHubProperties = eventHubProperties;
        this.connectorHealthActuatorEndpoint = connectorHealthActuatorEndpoint;
    }

    @Bean
    public EventHubClientFacade eventHubClientFacade() {
        return new EventHubClientFacade(eventHubProperties, connectorHealthActuatorEndpoint);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(eventHubProperties.getThreadPoolSize());
    }
}
