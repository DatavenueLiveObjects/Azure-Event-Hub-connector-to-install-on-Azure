/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class LoService {

    private final LOApiClient loApiClient;
    private final DataManagementFifo dataManagementFifo;
    private final RetryPolicy<Void> mqttRetryPolicy;

    public LoService(LOApiClient loApiClient,
                     RetryPolicy<Void> mqttRetryPolicy) {
        this.loApiClient = loApiClient;
        this.dataManagementFifo = loApiClient.getDataManagementFifo();
        this.mqttRetryPolicy = mqttRetryPolicy;
    }

    @PostConstruct
    public void start() {
        Failsafe.with(mqttRetryPolicy).run(
                () -> dataManagementFifo.connectAndSubscribe()
        );
    }

    @PreDestroy
    public void stop() {
        Failsafe.with(mqttRetryPolicy).run(
                () -> dataManagementFifo.disconnect()
        );
    }
}
