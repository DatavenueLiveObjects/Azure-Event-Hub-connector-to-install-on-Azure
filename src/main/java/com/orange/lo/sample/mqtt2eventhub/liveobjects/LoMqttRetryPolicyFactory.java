package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sdk.mqtt.exceptions.LoMqttException;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

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
