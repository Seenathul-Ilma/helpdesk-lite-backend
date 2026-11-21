# HelpDesk Lite API

A RESTful ticket management system built with **Java 25, Spring Boot 4, Spring Data JPA, and MySQL**. HelpDesk Lite provides APIs for creating, retrieving, filtering, assigning, and managing support tickets with controlled status transitions and status history tracking.

## Features

- **Ticket Management:** Create and retrieve support tickets.
- **Ticket Filtering:** Filter tickets by status and priority.
- **Status Management:** Update ticket status through a predefined workflow.
- **Status Validation:** Prevent invalid status transitions.
- **Assignee Management:** Assign or reassign support agents to tickets.
- **Status History:** Maintain a record of ticket status changes.
- **Request Validation:** Validate incoming requests using Jakarta Bean Validation.
- **Global Exception Handling:** Return consistent error responses.
- **Database Migrations:** Manage database schema using Flyway.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 25 | Programming language |
| Spring Boot 4.0.10 | Backend framework |
| Spring Web | REST API development |
| Spring Data JPA | Database persistence |
| Hibernate | ORM |
| MySQL 8 | Relational database |
| Flyway | Database migrations |
| Jakarta Validation | Request validation |
| Maven | Build and dependency management |
| Docker Compose | MySQL container management |
| JUnit 5 / Spring Boot Test | Testing |

## Prerequisites

Make sure you have the following installed:

- Java 25 or compatible configured JDK
- Docker Desktop
- Git
- Postman (for API testing)

Maven is managed through the included Maven Wrapper.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Seenathul-Ilma/helpdesk-lite-backend.git
cd helpdesk-lite
```

### 2. Start the MySQL database

Start the database container using Docker Compose:

```bash
docker compose up -d
```

Check the container status:

```bash
docker compose ps
```

Ensure the MySQL container is running and healthy before starting the application.

### 3. Configure database connection

The application uses environment variables for database configuration.

| Variable | Description |
|---|---|
| DB_URL | MySQL JDBC connection URL |
| DB_USERNAME | Database username |
| DB_PASSWORD | Database password |

The application configuration provides default values. Ensure the database credentials match those configured in `docker-compose.yml`.

### 4. Build the application

Windows PowerShell:

```powershell
.\mvnw.cmd clean compile
```

### 5. Run database migrations and start the application

```powershell
.\mvnw.cmd spring-boot:run
```

Flyway automatically applies pending migrations during application startup.

The API will be available at:

`http://localhost:8080`

### 6. Run tests

```powershell
.\mvnw.cmd test
```

## API Documentation

**Base URL**

```text
http://localhost:8080/api/tickets
```

### Available Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/tickets` | Create a new ticket |
| GET | `/api/tickets` | Retrieve all tickets |
| GET | `/api/tickets/{id}` | Retrieve a ticket by ID |
| PATCH | `/api/tickets/{id}/status` | Update ticket status |
| PATCH | `/api/tickets/{id}/assignee` | Assign or reassign an agent |
| GET | `/api/tickets/{id}/history` | Retrieve ticket status history |

### 1. Create a Ticket

**POST** `/api/tickets`

Request body:

```json
{
  "title": "Broken monitor",
  "description": "Screen flickering on my Dell monitor",
  "priority": "HIGH",
  "requesterName": "Alice Smith",
  "requesterEmail": "alice@example.com"
}
```

Expected response: `201 Created`

A newly created ticket automatically receives the `OPEN` status.

### 2. Retrieve All Tickets

**GET** `/api/tickets`

Retrieve all available tickets.

Optional query parameters:

- `status`
- `priority`

Examples:

```http
GET /api/tickets?status=OPEN
```

```http
GET /api/tickets?priority=HIGH
```

```http
GET /api/tickets?status=IN_PROGRESS&priority=HIGH
```

Expected response: `200 OK`

### 3. Retrieve a Ticket by ID

**GET** `/api/tickets/{id}`

Example:

```http
GET /api/tickets/1
```

Expected response: `200 OK`

Returns `404 Not Found` if the requested ticket does not exist.

### 4. Update Ticket Status

**PATCH** `/api/tickets/{id}/status`

Request body:

```json
{
  "status": "IN_PROGRESS",
  "actorRef": "agent1"
}
```

Expected response: `200 OK`

#### Supported Status Workflow

Tickets follow a predefined status transition sequence:

```text
OPEN
  |
  v
IN_PROGRESS
  |
  v
RESOLVED
  |
  v
CLOSED
```

Invalid transitions are rejected with `400 Bad Request`.

For example, transitioning directly from `OPEN` to `CLOSED` is not permitted.

### 5. Assign an Agent

**PATCH** `/api/tickets/{id}/assignee`

Request body:

```json
{
  "assigneeRef": "agent2"
}
```

Expected response: `200 OK`

Updates the ticket's assigned agent reference.

### 6. Retrieve Ticket Status History

**GET** `/api/tickets/{id}/history`

Example:

```http
GET /api/tickets/1/history
```

Expected response: `200 OK`

Example response:

```json
[
  {
    "id": 1,
    "oldStatus": "OPEN",
    "newStatus": "IN_PROGRESS",
    "actorRef": "agent1",
    "changedAt": "2026-11-21T10:30:00Z"
  },
  {
    "id": 2,
    "oldStatus": "IN_PROGRESS",
    "newStatus": "RESOLVED",
    "actorRef": "agent1",
    "changedAt": "2026-11-21T11:30:00Z"
  }
]
```

Each recorded entry represents a ticket status change.

## Ticket Priority

The supported priority levels are:

- `LOW`
- `MEDIUM`
- `HIGH`

## Ticket Status

The supported ticket statuses are:

- `OPEN`
- `IN_PROGRESS`
- `RESOLVED`
- `CLOSED`

## Error Handling

The application provides centralized exception handling through `GlobalExceptionHandler`.

| HTTP Status | Description |
|---|---|
| 400 Bad Request | Invalid request or status transition |
| 404 Not Found | Requested ticket does not exist |
| 500 Internal Server Error | Unexpected server-side error |

Example error response:

```json
{
  "status": 400,
  "error": "invalid_transition",
  "message": "Invalid transition from OPEN to CLOSED",
  "timestamp": "2026-11-21T10:30:00Z"
}
```

## Project Structure

```text
helpdesk-lite/
│
├── src/
│   ├── main/
│   │   ├── java/com/helpdesklite/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── enums/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── HelpdeskLiteApplication.java
│   │   │
│   │   └── resources/
│   │       ├── db/migration/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/com/helpdesklite/
│
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
└── AI-USE.md
```

## Design Decisions

1. **Layered Architecture:** Separates controllers, services, repositories, entities, DTOs, and exception handling.
2. **DTO Pattern:** Separates API request and response models from persistence entities.
3. **Enum-Based Status and Priority:** Uses predefined values to maintain consistency.
4. **Controlled Status Transitions:** Enforces valid ticket lifecycle transitions in the service layer.
5. **Status History Tracking:** Stores previous and new statuses, actor references, and timestamps.
6. **Flyway Migrations:** Uses version-controlled SQL migrations to manage the database schema.
7. **Global Exception Handling:** Provides structured error responses for API consumers.

## Testing

The application can be tested using:

- Maven test execution.
- Spring Boot controller tests.
- Postman API requests.
- Manual validation of ticket creation, filtering, status transitions, assignment, and history retrieval.

## AI Usage Disclosure

AI assistance was used during development for planning, implementation guidance, troubleshooting, and documentation.

See [AI-USE.md](AI-USE.md) for details.

## License

This project was developed as part of a Java Backend Practical Assessment.