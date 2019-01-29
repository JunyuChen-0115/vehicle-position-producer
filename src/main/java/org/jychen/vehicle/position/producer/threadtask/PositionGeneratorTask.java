package org.jychen.vehicle.position.producer.threadtask;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jychen.vehicle.position.api.integration.dto.VehiclePositionDTO;
import org.jychen.vehicle.position.producer.messaging.VehiclePositionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PositionGeneratorTask implements Callable<String> {

    private static final Logger logger = LoggerFactory.getLogger(PositionGeneratorTask.class);

    private String vehicleName;

    private List<String> vehiclePositions;

    private ObjectMapper objectMapper;

    private VehiclePositionProducer vehiclePositionProducer;

    @Autowired
    public PositionGeneratorTask(VehiclePositionProducer vehiclePositionProducer) {
        this.vehiclePositionProducer = vehiclePositionProducer;
        objectMapper = new ObjectMapper();
    }

    @Override
    public String call() throws Exception {
        for (String str : vehiclePositions) {
            String[] position = str.split("\"");
            double latitude = Double.valueOf(position[1]);
            double longitude = Double.valueOf(position[3]);
            VehiclePositionDTO vehiclePositionDTO = new VehiclePositionDTO();
            vehiclePositionDTO.setId(UUID.randomUUID());
            vehiclePositionDTO.setVehicleName(vehicleName);
            vehiclePositionDTO.setLatitude(latitude);
            vehiclePositionDTO.setLongitude(longitude);
            vehiclePositionDTO.setTs(new Timestamp(System.currentTimeMillis()));

            vehiclePositionProducer.sendMessage(vehiclePositionDTO);
            Thread.sleep((long) (Math.random() * 2000 + 1000));
        }

        return vehicleName;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public List<String> getVehiclePositions() {
        return vehiclePositions;
    }

    public void setVehiclePositions(List<String> vehiclePositions) {
        this.vehiclePositions = vehiclePositions;
    }
}
