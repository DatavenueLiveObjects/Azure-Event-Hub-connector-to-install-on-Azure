/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
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
    private final LoProperties loProperties;

    private final Counters counters;

    public LoService(LOApiClient loApiClient, Counters counters,
                     LoProperties loProperties) {
        this.dataManagementFifo = loApiClient.getDataManagementFifo();
        this.loProperties = loProperties;
        this.counters = counters;
    }

    @PostConstruct
    public void start() {
        try {
            dataManagementFifo.connect();
        } catch (LoMqttException e) {
            LOG.error("Problem with connection. Check LO credentials. ", e);
            counters.setLoConnectionStatus(false);
        }

        if (counters.isCloudConnectionStatusUp() && counters.isLoConnectionStatusUp())
            dataManagementFifo.connectAndSubscribe();
    }

    @PreDestroy
    public void stop() {
        dataManagementFifo.disconnect();
    }

    public void sendAck(int loMessageId) {
        dataManagementFifo.sendAck(loMessageId, loProperties.getMessageQos());
    }

}
