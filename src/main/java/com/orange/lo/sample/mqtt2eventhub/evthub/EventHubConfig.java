/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.evthub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class EventHubConfig {

    private final EventHubProperties eventHubProperties;

    public EventHubConfig(EventHubProperties eventHubProperties) {
        this.eventHubProperties = eventHubProperties;
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(eventHubProperties.getThreadPoolSize());
    }
}
