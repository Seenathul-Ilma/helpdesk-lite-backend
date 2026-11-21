# Helpdesk Lite

Helpdesk Lite is a lightweight REST API for creating and managing support tickets. It is built with Spring Boot and MySQL, and includes ticket priorities, status transitions, assignee references, and status-history records.

## Technology

- Java 25
- Spring Boot 4.0.10
- Spring Web MVC
- Spring Data JPA / Hibernate
- MySQL 8.4
- Flyway
- Bean Validation
- Maven
- Docker Compose

## Ticket model

### Priorities

- `LOW`
- `MEDIUM`
- `HIGH`

### Statuses

- `OPEN`
- `IN_PROGRESS`
- `RESOLVED`
- `CLOSED`

Status changes follow this transition sequence:

```text
OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED
```

Skipping statuses or changing a closed ticket is rejected.

## API

Base URL:

```text
http://localhost:8080
```

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/tickets` | Create a ticket |
| `GET` | `/api/tickets` | List tickets, optionally filtered by status and priority |
| `GET` | `/api/tickets/{id}` | Get a ticket |
| `PATCH` | `/api/tickets/{id}/status` | Transition a ticket to the next valid status |
| `PATCH` | `/api/tickets/{id}/assignee` | Assign or update an assignee reference |
| `GET` | `/api/tickets/{id}/history` | Get status-change history |

List filters:

```text
GET /api/tickets?status=OPEN
GET /api/tickets?priority=HIGH
GET /api/tickets?status=IN_PROGRESS&priority=HIGH
```

## Request examples

Create a ticket:

```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Broken monitor",
    "description": "The monitor flickers intermittently.",
    "priority": "HIGH",
    "requesterName": "Alice",
    "requesterEmail": "alice@example.com"
  }'
```

Update status:

```bash
curl -X PATCH http://localhost:8080/api/tickets/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IN_PROGRESS",
    "actorRef": "agent1"
  }'
```

Assign a ticket:

```bash
curl -X PATCH http://localhost:8080/api/tickets/1/assignee \
  -H "Content-Type: application/json" \
  -d '{
    "assigneeRef": "agent1"
  }'
```

## Local setup

### Prerequisites

- JDK 25
- Docker with Docker Compose

Docker is optional if you already run MySQL 8.x locally.

### 1. Start MySQL

```bash
docker compose up -d
```

This starts MySQL on port `3306`, creates the `helpdesk_db` database, and stores data in the `helpdesk_mysql_data` volume.

Default Docker Compose credentials:

- Database: `helpdesk_db`
- Username: `root`
- Password: `rootpassword`

### 2. Run the application

On Windows:

```bat
mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.

## Configuration

Configuration is read from `src/main/resources/application.properties` and may be overridden with environment variables:

| Environment variable | Default |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/helpdesk_db` |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | none |

Example:

```bash
DB_URL=jdbc:mysql://localhost:3306/helpdesk_db \
DB_USERNAME=root \
DB_PASSWORD=rootpassword \
./mvnw spring-boot:run
```

Flyway applies migrations automatically. Hibernate validation is enabled with `spring.jpa.hibernate.ddl-auto=validate`; it does not create or alter tables.

## Tests

Run the test suite:

```bash
./mvnw test
```

On Windows:

```bat
mvnw.cmd test
```

## Build

Create an executable JAR:

```bash
./mvnw clean package
```

The output is written to `target/helpdesk-lite-0.0.1-SNAPSHOT.jar`.

## Error responses

Errors use this JSON shape:

```json
{
  "status": 400,
  "error": "invalid_transition",
  "message": "Invalid transition from OPEN to CLOSED"
}
```

Common error codes:

- `not_found` - ticket does not exist
- `invalid_transition` - status transition is not allowed
- `validation_failed` - request validation failed
- `internal_error` - unexpected server error