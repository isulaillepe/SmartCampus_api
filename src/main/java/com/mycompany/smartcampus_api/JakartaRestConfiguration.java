package com.mycompany.smartcampus_api;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Configures Jakarta RESTful Web Services for the application.
 */
@ApplicationPath("/api/v1")
public class JakartaRestConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        // Explicitly register all your resource classes here so Tomcat NEVER misses
        // them
        resources.add(com.mycompany.smartcampus_api.resources.DiscoveryResource.class);
        resources.add(com.mycompany.smartcampus_api.resources.SensorRoomResource.class);
        resources.add(com.mycompany.smartcampus_api.resources.SensorResource.class);

        return resources;
    }
}