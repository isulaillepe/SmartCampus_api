package com.mycompany.smartcampus_api.resources;

import com.mycompany.smartcampus_api.dao.GenericDAO;
import com.mycompany.smartcampus_api.database.MockDatabase;
import com.mycompany.smartcampus_api.models.Room;
import com.mycompany.smartcampus_api.models.Sensor;
import com.mycompany.smartcampus_api.exceptions.LinkedResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sensors")
public class SensorResource {

    private GenericDAO<Sensor> sensorDao = new GenericDAO<>(MockDatabase.sensors);
    private GenericDAO<Room> roomDao = new GenericDAO<>(MockDatabase.rooms);

    /** Retrieves all sensors, optionally filtered by type. */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = sensorDao.getAll();

        if (type == null || type.trim().isEmpty()) {
            return Response.ok(allSensors).build();
        }

        List<Sensor> filteredSensors = allSensors.stream()
                .filter(sensor -> type.equalsIgnoreCase(sensor.getType()))
                .collect(Collectors.toList());

        return Response.ok(filteredSensors).build();
    }

    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorDao.getById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Sensor not found.").build();
        }
        return Response.ok(sensor).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        if (sensor.getId() == null || sensor.getRoomId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Both Sensor ID and Room ID are required.")
                    .build();
        }

        Room targetRoom = roomDao.getById(sensor.getRoomId());
        if (targetRoom == null) {
            throw new LinkedResourceNotFoundException(
                    "Cannot assign sensor. Room ID '" + sensor.getRoomId() + "' does not exist.");
        }

        Sensor createdSensor = sensorDao.add(sensor);
        targetRoom.getSensorIds().add(createdSensor.getId());
        roomDao.update(targetRoom);

        return Response.status(Response.Status.CREATED).entity(createdSensor).build();
    }

    /** Sub-resource locator for sensor readings. */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}