/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.mqtt2eventhub.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "metrics")
@Component
public class MetricsProperties {
    private String namespace;
    private String dimensionName;
    private String dimensionValue;
    private boolean sendToCloudwatch;
    private boolean useServiceProfile;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getDimensionValue() {
        return dimensionValue;
    }

    public void setDimensionValue(String dimensionValue) {
        this.dimensionValue = dimensionValue;
    }

    public boolean isSendToCloudwatch() {
        return sendToCloudwatch;
    }

    public void setSendToCloudwatch(boolean sendToCloudwatch) {
        this.sendToCloudwatch = sendToCloudwatch;
    }

    public boolean isUseServiceProfile() {
        return useServiceProfile;
    }

    public void setUseServiceProfile(boolean useServiceProfile) {
        this.useServiceProfile = useServiceProfile;
    }
}