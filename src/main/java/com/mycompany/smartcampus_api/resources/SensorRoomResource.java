

package com.mycompany.smartcampus_api.resources;

import com.mycompany.smartcampus_api.dao.GenericDAO;
import com.mycompany.smartcampus_api.database.MockDatabase;
import com.mycompany.smartcampus_api.models.Room;
import com.mycompany.smartcampus_api.exceptions.RoomNotEmptyException;

import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/rooms")
public class SensorRoomResource {

    private GenericDAO<Room> roomDao = new GenericDAO<>(MockDatabase.rooms);

    /** Retrieves all rooms. */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> rooms = roomDao.getAll();
        return Response.ok(rooms).build();
    }

    /** Creates a new room. */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Room ID cannot be empty.")
                           .build();
        }
        
        Room createdRoom = roomDao.add(room);
        return Response.status(Response.Status.CREATED).entity(createdRoom).build();
    }

    /** Retrieves detailed metadata for a specific room. */
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
    /** Deletes a room if it contains no active sensors. */
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

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room. Active sensors are still assigned.");

        }

        roomDao.delete(roomId);
        return Response.ok("Room successfully deleted.").build();
    }
}