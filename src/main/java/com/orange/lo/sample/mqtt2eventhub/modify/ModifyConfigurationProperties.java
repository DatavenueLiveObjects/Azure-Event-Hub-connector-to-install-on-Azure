package com.orange.lo.sample.mqtt2eventhub.modify;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModifyConfigurationProperties {

	private String loHostname;
    private String loApiKey;
    private String loTopic;
    private Integer loKeepAliveIntervalSeconds;
    private Boolean loAutomaticReconnect;
    private Integer loMessageQos;
    private Integer loConnectionTimeout;
    
    private String evtHubNameSpace;
    private String evtHubName;
    private String evtHubSasKeyName;
    private String evtHubSasKey;
    private Integer evtHubThreadPoolSize;
    private Long evtHubConnectionTimeout;
    private Integer evtHubTaskQueueSize;
    private Duration evtHubThrottlingDelay;
    private Integer evtHubMaxSendAttempts;
    
	public String getLoHostname() {
		return loHostname;
	}
	public void setLoHostname(String loHostname) {
		this.loHostname = loHostname;
	}
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
	public Integer getLoKeepAliveIntervalSeconds() {
		return loKeepAliveIntervalSeconds;
	}
	public void setLoKeepAliveIntervalSeconds(Integer loKeepAliveIntervalSeconds) {
		this.loKeepAliveIntervalSeconds = loKeepAliveIntervalSeconds;
	}
	public Boolean getLoAutomaticReconnect() {
		return loAutomaticReconnect;
	}
	public void setLoAutomaticReconnect(Boolean loAutomaticReconnect) {
		this.loAutomaticReconnect = loAutomaticReconnect;
	}
	public Integer getLoMessageQos() {
		return loMessageQos;
	}
	public void setLoMessageQos(Integer loMessageQos) {
		this.loMessageQos = loMessageQos;
	}
	public Integer getLoConnectionTimeout() {
		return loConnectionTimeout;
	}
	public void setLoConnectionTimeout(Integer loConnectionTimeout) {
		this.loConnectionTimeout = loConnectionTimeout;
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
	public String getEvtHubSasKeyName() {
		return evtHubSasKeyName;
	}
	public void setEvtHubSasKeyName(String evtHubSasKeyName) {
		this.evtHubSasKeyName = evtHubSasKeyName;
	}
	public String getEvtHubSasKey() {
		return evtHubSasKey;
	}
	public void setEvtHubSasKey(String evtHubSasKey) {
		this.evtHubSasKey = evtHubSasKey;
	}
	public Integer getEvtHubThreadPoolSize() {
		return evtHubThreadPoolSize;
	}
	public void setEvtHubThreadPoolSize(Integer evtHubThreadPoolSize) {
		this.evtHubThreadPoolSize = evtHubThreadPoolSize;
	}
	public Long getEvtHubConnectionTimeout() {
		return evtHubConnectionTimeout;
	}
	public void setEvtHubConnectionTimeout(Long evtHubConnectionTimeout) {
		this.evtHubConnectionTimeout = evtHubConnectionTimeout;
	}
	public Integer getEvtHubTaskQueueSize() {
		return evtHubTaskQueueSize;
	}
	public void setEvtHubTaskQueueSize(Integer evtHubTaskQueueSize) {
		this.evtHubTaskQueueSize = evtHubTaskQueueSize;
	}
	public Duration getEvtHubThrottlingDelay() {
		return evtHubThrottlingDelay;
	}
	public void setEvtHubThrottlingDelay(Duration evtHubThrottlingDelay) {
		this.evtHubThrottlingDelay = evtHubThrottlingDelay;
	}
	public Integer getEvtHubMaxSendAttempts() {
		return evtHubMaxSendAttempts;
	}
	public void setEvtHubMaxSendAttempts(Integer evtHubMaxSendAttempts) {
		this.evtHubMaxSendAttempts = evtHubMaxSendAttempts;
	}
	
	@Override
	public String toString() {
		return "ModifyConfigurationProperties [loHostname=" + loHostname + ", loApiKey=***" + ", loTopic="
				+ loTopic + ", loKeepAliveIntervalSeconds=" + loKeepAliveIntervalSeconds + ", loAutomaticReconnect="
				+ loAutomaticReconnect + ", loMessageQos=" + loMessageQos + ", loConnectionTimeout="
				+ loConnectionTimeout + ", evtHubNameSpace=" + evtHubNameSpace + ", evtHubName=" + evtHubName
				+ ", evtHubSasKeyName=***" + ", evtHubSasKey=***" 
				+ ", evtHubThreadPoolSize=" + evtHubThreadPoolSize + ", evtHubConnectionTimeout="
				+ evtHubConnectionTimeout + ", evtHubTaskQueueSize=" + evtHubTaskQueueSize + ", evtHubThrottlingDelay="
				+ evtHubThrottlingDelay + ", evtHubMaxSendAttempts=" + evtHubMaxSendAttempts + "]";
	}
}