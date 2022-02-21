/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sdk.mqtt.exceptions.LoMqttException;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
public class LoMqttRetryPolicyFactory {

    @Bean
    public RetryPolicy<Void> mqttRetryPolicy() {
        return new RetryPolicy<Void>()
                .handleIf(e -> e instanceof LoMqttException)
                .withMaxAttempts(-1)
                .withBackoff(1, 60, ChronoUnit.SECONDS)
                .withMaxDuration(Duration.ofHours(1));
    }
}
