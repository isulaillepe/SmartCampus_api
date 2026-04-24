/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus_api.database;

/**
 *
 * @author isulailleperuma
 */

import com.mycompany.smartcampus_api.models.Room;
import com.mycompany.smartcampus_api.models.Sensor;
import com.mycompany.smartcampus_api.models.SensorReading;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockDatabase {
    // Thread-safe in-memory storage simulating a database
    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public static final Map<String, SensorReading> readings = new ConcurrentHashMap<>();
}