/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.evthub;

public class EventHubClientFacadeException extends Exception {
    public EventHubClientFacadeException(Exception e) {
        super(e.getMessage(), e);
    }

    public EventHubClientFacadeException(String message) {
        super(message);
    }
}
