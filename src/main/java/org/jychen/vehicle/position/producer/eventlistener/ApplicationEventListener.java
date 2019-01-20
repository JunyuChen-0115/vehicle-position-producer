package org.jychen.vehicle.position.producer.eventlistener;

import org.apache.commons.io.FileUtils;
import org.jychen.vehicle.position.producer.threadtask.PositionGeneratorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class ApplicationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationEventListener.class);

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private ApplicationContext applicationContext;

    @Autowired
    public ApplicationEventListener(ThreadPoolTaskExecutor threadPoolTaskExecutor, ApplicationContext applicationContext) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.applicationContext = applicationContext;
    }

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) throws IOException, ExecutionException, InterruptedException {
        logger.info("Event {} is ready. Starting my codes...", event.toString());
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] availableResources = resourcePatternResolver.getResources("vehicle_data/city_truck");
        for (Resource resource : availableResources) {
            logger.info("File with name {} is found.", resource.getFilename());
            String vehicleName = resource.getFilename();
            int vehicleId = vehicleName.hashCode();
            List<String> vehiclePositions = FileUtils.readLines(resource.getFile());
            PositionGeneratorTask task = applicationContext.getBean(PositionGeneratorTask.class, vehicleId, vehicleName, vehiclePositions);
            Future<String> future = threadPoolTaskExecutor.submit(task);
            logger.info("isDone: {}", future.isDone());
            String result = future.get();
            logger.info("Result value is: {}", result);
        }
    }
}
