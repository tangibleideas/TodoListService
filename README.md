# Todo List Service

## Service Description

This service is a RESTful API for managing todo items.
It's built with Spring Boot, and uses database transactions wherever applicable, refer Tech Stack section for more details.

Two main flows on high level

- API Requests: API Client -> Controller -> Service -> Repository -> Database  [ + Exception Handlers]
- Scheduled Job: Scheduler -> Scheduled Job -> Service -> Repository -> Database [ + Exception Handlers ]

Features provided via API
- Add new todo items by specifying description and due_datetime
- Update status or description of a todo item based on business logic
- Get all 'not done' todo items, or get all items regardless of the status
- A scheduled job to mark 'not done' tasks as 'past due' when due date in past

**Assumptions:**
- There is no concept of user specific todo items
- Implementing database migrations is out of scope
- Telemetry is out of scope
- Rate limiting or caching is out of scope
- HATEOAS principles ( entity links in response are not necessary in the first version)
- Service will be evaluated as a single server, so, deployment considerations can be ignored, such as readiness probe in K8s or optimizing database connection pool etc.

**Further Optimizations which are not done to keep it timeboxed**
- Pagination
- Test coverage can be improved further
- Test implementation can be more consistent across different tests
- Spring boot profiles can be utilized for deployment in multiple environments (is trivial effort)
- application.yaml is currently development friendly can be optimized to disable extra logging
- health check is not added (is trivial effort)

## Tech Stack

- **Runtime Environment**: Java 21
- **Frameworks**: Spring Boot 3
- **Libraries**:
    - Spring Data JPA (for database interaction)
    - H2 Database (in-memory database)
    - Springdoc OpenAPI (for API documentation via Swagger UI)
    - Lombok (to reduce boilerplate code)
    - MapStruct (for DTO-entity mapping)
- **Build Tool**: Gradle
- **Code Formatting**: Spotless with Google Java Formatter
- **Containerization**: Docker (Layered Builds)

## How-To Guide

### Build the Service

To build the application navigate to the project root and run
- make sure java version is 21
- in case of issues consider docker approach, dockerfile is configured to build inside the container
```bash
./gradlew build
```

### Run the Automatic Tests

To execute the unit and integration tests, run:
```bash
./gradlew test
```
### Run the Service Locally without Docker
```bash
./gradlew bootRun
```

### Run the Service Locally Using Docker

1.  **Build the Docker image:**
    ```bash
    docker build -t todo-list-service .
    ```

2.  **Run the Docker container:**
    ```bash
    docker run -p 8080:8080 todo-list-service
    ```
The service will be accessible at `http://localhost:8080`. You can view the API documentation at `http://localhost:8080/swagger-ui.html`.

**Server port can be modified in `src/main/resources/application.yaml`** 
```bash
server:
  port: 8080
```

## CURL EXAMPLES

### Create a todo item
```bash
curl -X POST --location "http://localhost:8080/api/v1/todos" \
    -H "Content-Type: application/json" \
    -d '{
          "description": "My Task Description",
          "due_datetime": "2025-10-11T21:00:00.294160Z"
        }'
```

### Update description of a todo item
```bash
curl -X PATCH --location "http://localhost:8080/api/v1/todos/1" \
    -H "Content-Type: application/json" \
    -d '{
        "description": "New task description"
        }'
```

### Update status of a todo item
```bash
curl -X PATCH --location "http://localhost:8080/api/v1/todos/2" \
    -H "Content-Type: application/json" \
    -d '{
          "status": "done"
        }'
```

## Get all not done todo items
```bash
curl -X GET --location "http://localhost:8080/api/v1/todos" \
    -H "Content-Type: application/json"
```


### Get all todo items (regardless of the task status)
```bash
curl -X GET --location "http://localhost:8080/api/v1/todos?all=true" \
    -H "Content-Type: application/json"
```
