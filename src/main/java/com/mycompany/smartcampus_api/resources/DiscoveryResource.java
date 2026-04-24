/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus_api.resources;

/**
 *
 * @author isulailleperuma
 */
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Discovery Endpoint - Serves as the entry point for the Smart Campus API.
 */
@Path("/") 
public class DiscoveryResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON) // Tells JAX-RS to return JSON instead of plain text
    public Response getDiscoveryInfo() {
        
        // 1. Create a map for the API metadata required by the brief
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("api_name", "Smart Campus Management API");
        responseData.put("version", "v1.0");
        responseData.put("admin_contact", "admin@smartcampus.westminster.ac.uk");
        
        // 2. Create the HATEOAS navigation links
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        
        // 3. Attach links to the main response
        responseData.put("links", links);

        // 4. Return the HTTP 200 OK response with the JSON payload
        return Response.ok(responseData).build();
    }
}