package com.mycompany.smartcampus_api.resources;

import com.mycompany.smartcampus_api.dao.GenericDAO;
import com.mycompany.smartcampus_api.database.MockDatabase;
import com.mycompany.smartcampus_api.models.Sensor;
import com.mycompany.smartcampus_api.models.SensorReading;
import com.mycompany.smartcampus_api.exceptions.SensorUnavailableException;
import com.mycompany.smartcampus_api.exceptions.LinkedResourceNotFoundException;

import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class SensorReadingResource {

    private String parentSensorId;
    private GenericDAO<SensorReading> readingDao = new GenericDAO<>(MockDatabase.readings);
    private GenericDAO<Sensor> sensorDao = new GenericDAO<>(MockDatabase.sensors);

    public SensorReadingResource(String parentSensorId) {
        this.parentSensorId = parentSensorId;
    }

    /** Retrieves readings for the parent sensor. */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadingsForSensor() {
        List<SensorReading> readings = readingDao.getAll();
        return Response.ok(readings).build();
    }

    /** Adds a reading and updates the parent sensor's current value. */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor parentSensor = sensorDao.getById(parentSensorId);

        if (parentSensor == null) {
            throw new LinkedResourceNotFoundException("Parent sensor not found.");
        }

        // Prevent updates if sensor is in maintenance
        if ("MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Cannot accept readings. Sensor '" + parentSensorId + "' is currently in MAINTENANCE mode.");
        }

        if (reading.getId() == null) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        SensorReading createdReading = readingDao.add(reading);

        parentSensor.setCurrentValue(reading.getValue());
        sensorDao.update(parentSensor);

        return Response.status(Response.Status.CREATED).entity(createdReading).build();
    }
}