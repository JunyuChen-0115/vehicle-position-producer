package org.jychen.vehicle.position.producer.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jychen.vehicle.position.api.integration.dto.VehiclePositionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
            kafkaTemplate.send(TOPIC_NAME, vehiclePositionDTO);
        } catch (JsonProcessingException e) {
            logger.error("Failed to send message to queue.", e);
        }
    }
}
