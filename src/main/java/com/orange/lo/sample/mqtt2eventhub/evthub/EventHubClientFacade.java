package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.orange.lo.sample.mqtt2eventhub.utils.ConnectorHealthActuatorEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;


public class EventHubClientFacade {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventHubProperties eventHubProperties;

    private final ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint;

    public EventHubClientFacade(EventHubProperties eventHubProperties, ConnectorHealthActuatorEndpoint connectorHealthActuatorEndpoint) {
        this.eventHubProperties = eventHubProperties;
        this.connectorHealthActuatorEndpoint = connectorHealthActuatorEndpoint;
    }

    public EventHubClient eventHubClient() throws EventHubException, IOException {
        ConnectionStringBuilder conn = new ConnectionStringBuilder()
                .setOperationTimeout(Duration.of(eventHubProperties.getConnectionTimeout(), ChronoUnit.MILLIS))
                .setNamespaceName(eventHubProperties.getNameSpace())
                .setEventHubName(eventHubProperties.getEvtHubName())
                .setSasKeyName(eventHubProperties.getSasKeyName())
                .setSasKey(eventHubProperties.getSasKey());

        EventHubClient eventHubClient = null;
        try {
            eventHubClient = EventHubClient.createFromConnectionStringSync(conn.toString(), Executors.newScheduledThreadPool(eventHubProperties.getThreadPoolSize()));
        } catch (Exception e) {
            LOG.error("Problem with connection. Check Event Hub credentials. " + e.getMessage(), e);
            connectorHealthActuatorEndpoint.setCloudConnectionStatus(false);
        }

        return eventHubClient;
    }
}
