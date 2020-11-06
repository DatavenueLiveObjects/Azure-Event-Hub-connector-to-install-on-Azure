/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.mqtt2eventhub;

import com.orange.lo.sample.mqtt2eventhub.utils.Counters;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@EnableScheduling
@Component
public class CounterReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Counter mqttRead;
    private Counter evtAttempt;
    private Counter evtOK;
    private Counter evtKO;
    private Counter evtAborted;
    private Counter evtRetried;
    private Counter evtRejected;

     public CounterReader(Counters counterProvider) {
        mqttRead = counterProvider.mqttEvents();
        evtAttempt = counterProvider.evtAttemptCount();
        evtKO = counterProvider.evtFailure();
        evtOK = counterProvider.evtSuccess();
        evtAborted = counterProvider.evtAborted();
        evtRetried = counterProvider.evtRetried();
        evtRejected = counterProvider.evtRejected();
    }

    @Scheduled(fixedRate = 30000)
    public void dumpMqtt() {
        LOGGER.info("mqtt received: {}", val(mqttRead));
    }

    @Scheduled(fixedRate = 30000)
    public void dumpEvt() {
        LOGGER.info("EvtSend attempted/rejected: {}/{}", val(evtAttempt), val(evtRejected));  //EvtSend can be either an attempt or reject
        LOGGER.info("attempted EvtSend OK/abort: {}/{}, evt KO: {}, retries: {}", val(evtOK), val(evtAborted), val(evtKO), val(evtRetried)); //EvtSend attempt can be either OK or abort
    }

    private long val(Counter cnt) {
        return Math.round(cnt.count());
    }
}
