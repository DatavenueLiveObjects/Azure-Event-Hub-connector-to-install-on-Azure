/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.mqtt2eventhub;

import com.orange.lo.sample.mqtt2eventhub.utils.MetricsProperties;
import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

import java.lang.invoke.MethodHandles;

@SpringBootApplication
public class ConnectorApplication {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final MetricsProperties metricsProperties;

	ConnectorApplication(MetricsProperties metricsProperties) {
		this.metricsProperties = metricsProperties;
	}

	public static void main(String[] args) {
		SpringApplication.run(ConnectorApplication.class, args);
	}

	@Bean
	public MeterRegistry meterRegistry() {
		CloudWatchMeterRegistry cloudWatchMeterRegistry = new CloudWatchMeterRegistry(cloudWatchConfig(), Clock.SYSTEM, CloudWatchAsyncClient.create());
		cloudWatchMeterRegistry.config()
				.meterFilter(MeterFilter.deny(id -> !id.getName().startsWith("message")))
				.commonTags(metricsProperties.getDimensionName(), metricsProperties.getDimensionValue());
		return cloudWatchMeterRegistry;
	}

	private CloudWatchConfig cloudWatchConfig() {
		return new CloudWatchConfig() {

			@Override
			public String get(String key) {
				return null;
			}

			@Override
			public String namespace() {
				return metricsProperties.getNamespace();
			}
		};
	}
}