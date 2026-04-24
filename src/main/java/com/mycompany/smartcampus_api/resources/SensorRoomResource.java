

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus_api.resources;

/**
 *
 * @author isulailleperuma
 */

import com.mycompany.smartcampus_api.dao.GenericDAO;
import com.mycompany.smartcampus_api.database.MockDatabase;
import com.mycompany.smartcampus_api.models.Room;
import com.mycompany.smartcampus_api.exceptions.RoomNotEmptyException;

import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/rooms") // This maps to /api/v1/rooms
public class SensorRoomResource {

    // Instantiate the DAO for Rooms, pointing it to our static MockDatabase
    private GenericDAO<Room> roomDao = new GenericDAO<>(MockDatabase.rooms);

    // GET / - Provide a comprehensive list of all rooms
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> rooms = roomDao.getAll();
        return Response.ok(rooms).build();
    }

    // POST / - Enable the creation of new rooms
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        // Basic validation: ensure the room has an ID before adding
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Room ID cannot be empty.")
                           .build();
        }
        
        Room createdRoom = roomDao.add(room);
        return Response.status(Response.Status.CREATED).entity(createdRoom).build();
    }

    // GET /{roomId} - Fetch detailed metadata for a specific room
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = roomDao.getById(roomId);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Room not found.")
                           .build();
        }
        return Response.ok(room).build();
    
    }
    // DELETE /{roomId} - Allow room decommissioning with safety logic
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = roomDao.getById(roomId);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Room not found.")
                           .build();
        }

        // Business Logic Constraint: Check for active sensors
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
    // We just throw the exception! The Mapper does the rest.
    throw new RoomNotEmptyException("Cannot delete room. Active sensors are still assigned.");

        }

        roomDao.delete(roomId);
        return Response.ok("Room successfully deleted.").build();
    }
}