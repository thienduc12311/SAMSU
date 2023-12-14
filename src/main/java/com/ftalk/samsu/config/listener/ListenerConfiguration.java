package com.ftalk.samsu.config.listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ListenerConfiguration {
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(8);
        threadPoolTaskExecutor.setMaxPoolSize(16);
        threadPoolTaskExecutor.initialize();
        eventMulticaster.setTaskExecutor(threadPoolTaskExecutor);
        return eventMulticaster;
    }
}
