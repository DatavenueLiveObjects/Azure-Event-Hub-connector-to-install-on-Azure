package com.orange.lo.sample.mqtt2eventhub.evthub;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolExecutorFactoryImpl {

    @Bean
    private ThreadPoolExecutor threadPoolExecutor(EventHubProperties eventHubProperties) {
        BlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(eventHubProperties.getTaskQueueSize());
        return new ThreadPoolExecutor(eventHubProperties.getThreadPoolSize(),
                eventHubProperties.getThreadPoolSize(),
                10,
                TimeUnit.SECONDS,
                tasks);
    }
}
