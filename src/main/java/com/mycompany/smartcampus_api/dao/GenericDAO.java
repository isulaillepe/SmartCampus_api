/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus_api.dao;

/**
 *
 * @author isulailleperuma
 */
import com.mycompany.smartcampus_api.models.BaseModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericDAO<T extends BaseModel> {

    // The specific database "table" (map) this DAO will operate on
    private final Map<String, T> databaseTable;

    public GenericDAO(Map<String, T> databaseTable) {
        this.databaseTable = databaseTable;
    }

    // Retrieve all records as a List
    public List<T> getAll() {
        return new ArrayList<>(databaseTable.values());
    }

    // Retrieve a single record by its ID
    public T getById(String id) {
        return databaseTable.get(id);
    }

    // Add a new record
    public T add(T entity) {
        databaseTable.put(entity.getId(), entity);
        return entity;
    }

    // Update an existing record
    public T update(T entity) {
        if (databaseTable.containsKey(entity.getId())) {
            databaseTable.put(entity.getId(), entity);
            return entity;
        }
        return null; // Return null if the entity doesn't exist to update
    }

    // Delete a record by its ID
    public T delete(String id) {
        return databaseTable.remove(id);
    }
}