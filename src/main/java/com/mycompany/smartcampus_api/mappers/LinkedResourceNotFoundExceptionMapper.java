/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus_api.mappers;

/**
 *
 * @author isulailleperuma
 */


import com.mycompany.smartcampus_api.exceptions.LinkedResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unprocessable Entity");
        errorResponse.put("message", exception.getMessage());
        
        // 422 Unprocessable Entity as mandated by the spec
        return Response.status(422).entity(errorResponse).build();
    }
}