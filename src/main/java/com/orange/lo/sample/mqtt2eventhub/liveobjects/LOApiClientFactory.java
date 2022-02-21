/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.liveobjects;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.LOApiClientParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LOApiClientFactory {

    @Bean
    public LOApiClient LOApiClient(LOApiClientParameters parameters) {
        return new LOApiClient(parameters);
    }
}
