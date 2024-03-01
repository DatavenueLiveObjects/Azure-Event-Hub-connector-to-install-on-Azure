/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class EventHubConfig {

    private final EventHubProperties eventHubProperties;

    public EventHubConfig(EventHubProperties eventHubProperties) {
        this.eventHubProperties = eventHubProperties;
    }

    @Bean
    public EventHubClient eventHubClient() throws EventHubException, IOException {
        ConnectionStringBuilder conn = new ConnectionStringBuilder()
                .setOperationTimeout(Duration.of(eventHubProperties.getConnectionTimeout(), ChronoUnit.MILLIS))
                .setNamespaceName(eventHubProperties.getNameSpace())
                .setEventHubName(eventHubProperties.getEvtHubName())
                .setSasKeyName(eventHubProperties.getSasKeyName())
                .setSasKey(eventHubProperties.getSasKey());

        return EventHubClient.createFromConnectionStringSync(conn.toString(), Executors.newScheduledThreadPool(eventHubProperties.getThreadPoolSize()));
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(eventHubProperties.getThreadPoolSize());
    }
}
