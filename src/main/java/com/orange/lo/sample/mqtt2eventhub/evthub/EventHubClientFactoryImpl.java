/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.evthub;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import static com.microsoft.azure.eventhubs.EventHubClient.createFromConnectionStringSync;

@Component
public class EventHubClientFactoryImpl implements EventHubClientFactory {

    @Override
    public EventHubClient createEventHubClient(ConnectionStringBuilder conn, ScheduledExecutorService executor)
            throws IOException, EventHubException {
        return createFromConnectionStringSync(conn.toString(), executor);
    }
}
