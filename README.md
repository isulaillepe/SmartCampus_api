# Smart Campus API

A JAX-RS RESTful Web Service built to manage rooms and sensors within a smart campus environment. This API provides functionalities to create and manage rooms, allocate sensors to rooms, and record sensor readings.

## Technologies Used

- **Java:** JDK 17
- **Framework:** Jakarta EE (JAX-RS / Jersey)
- **Build Tool:** Maven (via Maven Wrapper)
- **Container:** Apache Tomcat 9 (via Cargo Maven Plugin)

## File Structure

src/main/java/com/mycompany/smartcampus_api
├── dao/ # Data Access Objects (Generic DAO)
├── database/ # MockDatabase Singleton
├── exceptions/ # Custom RuntimeExceptions (403, 409, 422)
├── filters/ # LoggingFilter (Observability)
├── mappers/ # ExceptionMappers
├── models/ # POJOs (Room, Sensor, Reading)
└── resources/ # REST Endpoints (Sub-resource Locators)

## How to Run Locally

You do not need to install Maven or Tomcat on your machine. This project uses the Maven Wrapper (`mvnw`) and the Cargo plugin to automatically download and run the server.

1. Open your terminal and navigate to the project directory:

   ```bash
   cd SmartCampus_api
   ```

2. Clean, build, and start the Tomcat server:

   ```bash
   ./mvnw clean package cargo:run
   ```

3. The server will start and host the API on port `5009`.

## API Endpoints

The base path for all API endpoints is: `http://localhost:5009/SmartCampus_api/api/v1`

### 1. Discovery

- `GET /`
  - Returns the API metadata and HATEOAS links to available endpoints.

### 2. Rooms (`/rooms`)

- `GET /rooms` - Retrieves a list of all rooms.
- `POST /rooms` - Creates a new room.
- `GET /rooms/{roomId}` - Retrieves metadata for a specific room.
- `DELETE /rooms/{roomId}` - Deletes a room (Only allowed if no active sensors are assigned).

### 3. Sensors (`/sensors`)

- `GET /sensors` - Retrieves a list of all sensors.
- `GET /sensors?type={type}` - Retrieves sensors filtered by a specific type (e.g., "temperature").
- `GET /sensors/{sensorId}` - Retrieves details for a specific sensor.
- `POST /sensors` - Creates a new sensor and assigns it to a room.

### 4. Sensor Readings (`/sensors/{sensorId}/readings`)

- `GET /sensors/{sensorId}/readings` - Retrieves all readings for a specific sensor.
- `POST /sensors/{sensorId}/readings` - Records a new reading and updates the parent sensor's current value. (Returns `403 Forbidden` if the sensor is in "MAINTENANCE" mode).

## Error Handling

This API uses standard HTTP status codes, mapped via Custom Exception Mappers:

- `200 OK` / `201 Created` - Successful operations.
- `400 Bad Request` - Missing IDs or invalid inputs.
- `404 Not Found` - Resource (Room/Sensor) does not exist.
- `403 Forbidden` - Business logic constraint violation (e.g., trying to add a reading to a sensor in maintenance mode).
- `409 Conflict` - Deleting a room that still has active sensors.
- `500 Internal Server Error` - Unexpected server issues.
