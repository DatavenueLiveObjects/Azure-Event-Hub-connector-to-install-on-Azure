/**
 * Copyright (c) Orange, Inc. and its affiliates. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.utils;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class ConnectorHealthActuatorEndpoint implements HealthIndicator {

    private Counters counters;

    public ConnectorHealthActuatorEndpoint(Counters counters) {
        this.counters = counters;
    }

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder(Status.UP);

        builder.withDetail("loMqttConnectionStatus", counters.isLoConnectionStatusUp());
        builder.withDetail("cloudConnectionStatus", counters.isCloudConnectionStatusUp());
        return builder.build();
    }
}