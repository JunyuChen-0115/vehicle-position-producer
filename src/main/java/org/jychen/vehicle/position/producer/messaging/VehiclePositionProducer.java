package org.jychen.vehicle.position.producer.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jychen.vehicle.position.api.integration.dto.VehiclePositionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class VehiclePositionProducer {

    private static final Logger logger = LoggerFactory.getLogger(VehiclePositionProducer.class);

    private static final String TOPIC_NAME = "first-topic";

    private ObjectMapper objectMapper = new ObjectMapper();

    private KafkaTemplate<String, VehiclePositionDTO> kafkaTemplate;

    @Autowired
    public VehiclePositionProducer(KafkaTemplate<String, VehiclePositionDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(VehiclePositionDTO vehiclePositionDTO) {
        try {
            logger.info("Sending position record: {}", objectMapper.writeValueAsString(vehiclePositionDTO));
            ListenableFuture<SendResult<String, VehiclePositionDTO>> future =  kafkaTemplate.send(TOPIC_NAME, vehiclePositionDTO);
            future.addCallback(new ListenableFutureCallback<SendResult<String, VehiclePositionDTO>>() {
                @Override
                public void onFailure(Throwable ex) {
                    logger.error("An error occurred during send.", ex);
                }

                @Override
                public void onSuccess(SendResult<String, VehiclePositionDTO> result) {
                }
            });
        } catch (JsonProcessingException e) {
            logger.error("Failed to send message to queue.", e);
        }
    }
}
