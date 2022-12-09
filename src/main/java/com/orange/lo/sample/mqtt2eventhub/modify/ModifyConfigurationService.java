package com.orange.lo.sample.mqtt2eventhub.modify;

import org.springframework.stereotype.Component;

@Component
public class ModifyConfigurationService {

    public ModifyConfigurationService() {
    }

    public ModifyConfigurationProperties getProperties() {

        return new ModifyConfigurationProperties();
    }

    public void modify(ModifyConfigurationProperties modifyConfigurationProperties) {

    }
}