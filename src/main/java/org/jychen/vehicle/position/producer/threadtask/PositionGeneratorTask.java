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
import java.util.concurrent.Callable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PositionGeneratorTask implements Callable<String> {

    private static final Logger logger = LoggerFactory.getLogger(PositionGeneratorTask.class);

    private int id;

    private String vehicleName;

    private List<String> vehiclePositions;

    private ObjectMapper objectMapper;

    @Autowired
    private VehiclePositionProducer vehiclePositionProducer;

    public PositionGeneratorTask(int id, String vehicleName, List<String> vehiclePositions) {
        this.id = id;
        this.vehicleName = vehicleName;
        this.vehiclePositions = vehiclePositions;
        objectMapper = new ObjectMapper();
    }

    @Override
    public String call() throws Exception {
        for (String str : vehiclePositions) {
            String[] position = str.split("\"");
            double latitude = Double.valueOf(position[1]);
            double longitude = Double.valueOf(position[3]);
            VehiclePositionDTO vehiclePositionDTO = new VehiclePositionDTO();
            vehiclePositionDTO.setId(id);
            vehiclePositionDTO.setVehicleName(vehicleName);
            vehiclePositionDTO.setLatitude(latitude);
            vehiclePositionDTO.setLongitude(longitude);
            vehiclePositionDTO.setTimestamp(new Timestamp(System.currentTimeMillis()));

            vehiclePositionProducer.sendMessage(vehiclePositionDTO);
            //logger.info("Waiting for next position record...");
            Thread.sleep((long) (Math.random() * 1000));
        }

        return vehicleName;
    }

    public int getId() {
        return id;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public List<String> getVehiclePositions() {
        return vehiclePositions;
    }
}
