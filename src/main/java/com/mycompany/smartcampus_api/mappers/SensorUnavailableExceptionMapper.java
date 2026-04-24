package com.mycompany.smartcampus_api.mappers;

import com.mycompany.smartcampus_api.exceptions.SensorUnavailableException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", exception.getMessage());

        // Return 403 Forbidden as mandated by the spec
        return Response.status(Response.Status.FORBIDDEN).entity(errorResponse).build();
    }
}