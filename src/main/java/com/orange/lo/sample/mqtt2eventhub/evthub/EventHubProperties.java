/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.mqtt2eventhub.evthub;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
@ConfigurationProperties(prefix = "azure.evt-hub")
public class EventHubProperties {
    private String nameSpace;
    private String evtHubName;
    private String sasKeyName;
    private String sasKey;
    private int threadPoolSize;
    private long connectionTimeout;
    private int taskQueueSize;
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration throttlingDelay = Duration.ofMillis(1);
    private int maxSendAttempts = 3;
    private Integer messageBatchSize;

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getEvtHubName() {
        return evtHubName;
    }

    public void setEvtHubName(String evtHubName) {
        this.evtHubName = evtHubName;
    }

    public String getSasKeyName() {
        return sasKeyName;
    }

    public void setSasKeyName(String sasKeyName) {
        this.sasKeyName = sasKeyName;
    }

    public String getSasKey() {
        return sasKey;
    }

    public void setSasKey(String sasKey) {
        this.sasKey = sasKey;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getTaskQueueSize() {
        return taskQueueSize;
    }

    public void setTaskQueueSize(int taskQueueSize) {
        this.taskQueueSize = taskQueueSize;
    }

    public Duration getThrottlingDelay() {
        return throttlingDelay;
    }

    public void setThrottlingDelay(Duration throttlingDelay) {
        this.throttlingDelay = throttlingDelay;
    }

    public int getMaxSendAttempts() {
        return maxSendAttempts;
    }

    public void setMaxSendAttempts(int maxSendAttempts) {
        this.maxSendAttempts = maxSendAttempts;
    }

    public Integer getMessageBatchSize() {
        return messageBatchSize;
    }

    public void setMessageBatchSize(Integer messageBatchSize) {
        this.messageBatchSize = messageBatchSize;
    }
}
