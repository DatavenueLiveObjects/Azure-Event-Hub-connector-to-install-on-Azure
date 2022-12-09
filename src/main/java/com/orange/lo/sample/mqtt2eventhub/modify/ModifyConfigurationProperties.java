package com.orange.lo.sample.mqtt2eventhub.modify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModifyConfigurationProperties {

    private String loApiKey;
    private String loTopic;

    private String evtHubNameSpace;
    private String evtHubName;
    private String sasKeyName;
    private String sasKey;

    public String getLoApiKey() {
        return loApiKey;
    }

    public void setLoApiKey(String loApiKey) {
        this.loApiKey = loApiKey;
    }

    public String getLoTopic() {
        return loTopic;
    }

    public void setLoTopic(String loTopic) {
        this.loTopic = loTopic;
    }

    public String getEvtHubNameSpace() {
        return evtHubNameSpace;
    }

    public void setEvtHubNameSpace(String evtHubNameSpace) {
        this.evtHubNameSpace = evtHubNameSpace;
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

    @Override
    public String toString() {
        return "ModifyConfigurationProperties{" +
                "loApiKey='" + loApiKey + '\'' +
                ", loTopic='" + loTopic + '\'' +
                ", evtHubNameSpace='" + evtHubNameSpace + '\'' +
                ", evtHubName='" + evtHubName + '\'' +
                ", sasKeyName='" + sasKeyName + '\'' +
                ", sasKey='" + sasKey + '\'' +
                '}';
    }
}