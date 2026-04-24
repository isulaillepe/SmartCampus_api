# Smart Campus API

A JAX-RS RESTful Web Service built to manage rooms and sensors within a smart campus environment. This API provides functionalities to create and manage rooms, allocate sensors to rooms, and record sensor readings.

## Main Features

1. Resource Management: Rooms and sensors can be created and managed.
2. Sensor Allocation: Sensors can be allocated to rooms.
3. Sensor Readings: Sensor readings can be recorded and retrieved.

## Technologies Used

- **Java:** JDK 17
- **Framework:** Jakarta EE (JAX-RS / Jersey)
- **Build Tool:** Maven (via Maven Wrapper)
- **Container:** Apache Tomcat 9 (via Cargo Maven Plugin)

## File Structure

```bash
src/main/java/com/mycompany/smartcampus_api
├── dao/ # Data Access Objects (Generic DAO)
├── database/ # MockDatabase Singleton
├── exceptions/ # Custom RuntimeExceptions (403, 409, 422)
├── filters/ # LoggingFilter (Observability)
├── mappers/ # ExceptionMappers
├── models/ # POJOs (Room, Sensor, Reading)
└── resources/ # REST Endpoints (Sub-resource Locators)
```

## How to Run Locally

Option 1-

1. Open Project: In NetBeans, go to File > Open Project and select the SmartCampus_api folder.

2. Clean and Build: Right-click the project name in the left-hand sidebar and select Clean and Build. This will trigger Maven to download the dependencies.

3. Run the Server: Right-click the project name and select Run.

Note: The project is configured to use the Cargo Plugin, which will automatically start an embedded Tomcat server.

Verification: Look for the message [INFO] [talledLocalContainer] Tomcat 9.x started on port [5009] in the NetBeans output console.

You do not need to install Maven or Tomcat on your machine. This project uses the Maven Wrapper (`mvnw`) and the Cargo plugin to automatically download and run the server.
If so,

Option 2 -

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

The base path for API endpoints is: `http://localhost:5009/SmartCampus_api/api/v1`

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

## curl commands

- `GET /rooms` - Retrieves a list of all rooms.
  ```bash
  curl -X GET "http://localhost:5009/SmartCampus_api/api/v1/rooms"
  ```
- `POST /rooms` - Creates a new room.
  ```bash
  curl -X POST "http://localhost:5009/SmartCampus_api/api/v1/rooms" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Room 101\",\"capacity\":25}"
  ```
- `GET /rooms/{roomId}` - Retrieves metadata for a specific room.
  ```bash
  curl -X GET "http://localhost:5009/SmartCampus_api/api/v1/rooms/1"
  ```
- `DELETE /rooms/{roomId}` - Deletes a room (Only allowed if no active sensors are assigned).
  ```bash
  curl -X DELETE "http://localhost:5009/SmartCampus_api/api/v1/rooms/1"
  ```
- `POST /rooms/{roomId}` - Register a Sensor(Linked to the room).
  ```bash
  curl -X POST "curl -X POST http://localhost:5009/SmartCampus_api/api/v1/sensors"
  ```

## Final Submission Answers

Smart Campus API - Conceptual Report
Part 1: Service Architecture & Setup

1.1 Project & Application Configuration

Question: In your report, explain the default lifecycle of a JAX-RS Resource class. Is a
new instance instantiated for every incoming request, or does the runtime treat it as a
singleton? Elaborate on how this architectural decision impacts the way you manage and
synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

- Answer: JAX-RS resource classes request scoped therefore by default for every incoming HTTP request a brand-new instance will be created and will be disposed of once the event is sent. Because with every request a instant variable is wiped out we must use a static collection in our mock database where it is able to persist data. Since simultaneously multiple threads access these static lists. A thread safe structure or a synchronised block should be used to prevent race conditions and ConcurrentModificationException during crud operations.

  1.2 The "Discovery" Endpoint

Question: Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

- Answer: Hypermedia (HATEOAS) is allowing a client to discover available resources dynamically through the links provided by JSON. This is beneficial for developers by reducing hard recording of the URL and API structure changes can be correctly navigated by the clients using the following links making the system more resilient and self dominant compared to other static documentations.

Part 2: Data Modelling and Persistence

2.1 Room Implementation

Question: When returning a list of rooms, what are the implications of returning only
IDs versus returning the full room objects? Consider network bandwidth and client side
processing.

- Answer: the response size is only minimise by returning only the IDs, which saves the Bandwidth and clients processing time. However, if the client is to take additional HTTP request to fetch the details fully for each room, it will increase the overall latency and the number of roundtrips , therefore returning full logs is more convenient for the client and it will reduce the number of request but uses more bandwidth.

  2.2 Room Deletion Logic

Question: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times

- Answer: Yes, the DELETE operation is idempotent. If the same delete request is sent by the client multiple times the first request will be successfully delete the Room and return 204 status. While the other requests will attempt to delete the same room but since it doesn’t exist in the database, they will return 404 not found status. Although the respond body differs the resource of the server remains deleted after the first attempt fulfiling the idempotent requirement.

Part 3: Sensor Operations & Linking

3.1 Sensor Resource & Integrity

Question: We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

- Answer: The JAX-RS runtime uses MessageBodyReaders which deserialises the incoming request bodies. If the endpoints @Consumes(MediaType.APPLICATION_JSON) doesn’t match the clients data in format (e.g., XML), the runtime will fail to find a compatible MessageBodyReader. Which will result in a 415 unsupported media type being returned to the client. As the server can not process the requested payload

  3.2 Filtered Retrieval & Search

Question: You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

- Answer: Using @QueryParam for filtering is generally superior because it is more flexible and RESTful. Path parameters (like /sensors/type/CO2) are typically used for identifying specific resources, while query parameters (like /sensors?type=CO2) are used for filtering or modifying a collection of resources. Query parameters also allow for multiple filtering criteria to be combined easily (e.g., /sensors?type=CO2&status=active), which is harder to achieve with path parameters.

Part 4: Deep Nesting with Sub-Resources

4.1 The Sub-Resource Locator Pattern

Question: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

- Answer: The Sub-Resource Locator pattern provides significant architectural benefits by promoting modularity and separation of concerns. Instead of having a single, monolithic controller class responsible for handling all nested routes, this pattern allows us to delegate requests for specific sub-resources to specialized locator methods within the same or different classes. For example, the /sensors/{sensorId}/readings path is handled by the SensorResource class, which acts as a locator, delegating the actual GET and POST operations to the ReadingResource class. This approach makes the codebase cleaner, more organized, and easier to maintain, as related functionality is grouped together, reducing complexity and improving code readability.

Part 5: Advanced Error Handling & Security

5.1 Dependency Validation (422 Unprocessable Entity)

Question: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

- Answer: HTTP 422 Unprocessable Entity is considered more semantically accurate than a 404 Not Found in this scenario because the client has sent a perfectly valid JSON payload that is syntactically correct. The server understands the data format, but the _content_ of the payload violates a business rule—specifically, attempting to assign a non-existent sensor to a room. A 404 implies the _resource_ (the room) cannot be found on the server, whereas a 422 correctly indicates that the server understood the request but could not process it due to semantic errors or violated constraints within the payload itself.

  5.2 The Global Safety Net (500)

Question: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

- Answer: exposing internal Java stack traces to external API consumers is extremely risky from a cyber security standpoint because stack traces contain sensitive information about details of internal implementation of the application. This can be used to find file parts , library versions , Class names as well as exact sequence of methods called that led to an error. Attacker can use this to perform detailed reconnaissance of the system, to identify potential vulnerabilities, and can craft targeted exploits or it might expose the internal file structure of the server, which could help an attacker map the system and identify other potential attack vectors. In a production environment, such information should be sanitized and replaced with generic error messages to prevent attackers from gaining valuable insights into the system's architecture and potential weaknesses.

  5.4 API Request & Response Logging Filters

Question: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like
logging, rather than manually inserting Logger.info() statements inside every single re-
source method?

- Answer: Using JAX-RS filters for logging is advantageous because it promotes separation of concerns and reduces code duplication. Filters allow you to define a centralized logging mechanism that intercepts all incoming requests and outgoing responses, regardless of the specific resource method being invoked. This eliminates the need to manually add logging statements to every method, making the codebase cleaner and more maintainable. Additionally, filters provide a consistent way to handle logging, ensuring that all requests and responses are logged in the same format. This can be particularly useful for debugging and monitoring, as it allows you to track the flow of requests through the API and identify any issues that may arise.
