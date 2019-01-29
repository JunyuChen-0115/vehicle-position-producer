package org.jychen.vehicle.position.producer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ComponentScan(basePackages = {
        "org.jychen.vehicle.position.producer.eventlistener",
        "org.jychen.vehicle.position.producer.messaging",
        "org.jychen.vehicle.position.producer.threadtask"})
public class CoreConfig {

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        // A cached thread pool instance to allow one thread per task
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(0);
        threadPoolTaskExecutor.setQueueCapacity(0);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
