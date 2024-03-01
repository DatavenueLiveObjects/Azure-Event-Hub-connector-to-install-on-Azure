/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.fifomqtt.DataManagementFifo;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class LoService {

    private final DataManagementFifo dataManagementFifo;

    public LoService(LOApiClient loApiClient) {
        this.dataManagementFifo = loApiClient.getDataManagementFifo();
    }

    @PostConstruct
    public void start() {
        dataManagementFifo.connectAndSubscribe();
    }

    @PreDestroy
    public void stop() {
        dataManagementFifo.disconnect();
    }
}
