# CareSync

An event-driven appointment scheduling and notification system built with microservices.

## Architecture

```
┌──────────────────────┐         ┌─────────────────┐         ┌──────────────────────────┐
│  appointment-service │──event──▶    RabbitMQ      │──event──▶  notification-service    │
│  (REST API + DB)     │         │  caresync.       │         │  (Email via JavaMail)    │
│  port: 8081          │         │  exchange        │         │  port: 8082              │
└──────────────────────┘         └─────────────────┘         └──────────────────────────┘
         │
         ▼
   PostgreSQL
```

## Services

### appointment-service (port 8081)
Handles appointment creation, persistence, and publishes `AppointmentScheduledEvent` to RabbitMQ.

### notification-service (port 8082)
Listens for events on RabbitMQ and sends a confirmation email to the patient.

## Tech Stack

| Layer        | Technology                  |
|--------------|-----------------------------|
| Language     | Java 21                     |
| Framework    | Spring Boot 3.3             |
| Messaging    | RabbitMQ + Spring AMQP      |
| Database     | PostgreSQL + Spring Data JPA |
| DTOs/Events  | Java Records                |
| Email        | Spring Mail (SMTP)          |
| Containers   | Docker + Docker Compose     |

## Running Locally

### 1. Start infrastructure

```bash
docker-compose up -d
```

This starts PostgreSQL on port 5432 and RabbitMQ on port 5672.  
RabbitMQ Management UI: [http://localhost:15672](http://localhost:15672) (guest/guest)

### 2. Configure email credentials

Set environment variables before starting `notification-service`:

```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

> Use a Gmail App Password, not your account password.

### 3. Start the services

```bash
# Terminal 1
cd appointment-service
./mvnw spring-boot:run

# Terminal 2
cd notification-service
./mvnw spring-boot:run
```

## API Reference

### Schedule an Appointment

```http
POST http://localhost:8081/api/appointments
Content-Type: application/json

{
  "patientName": "John Doe",
  "patientEmail": "john.doe@example.com",
  "doctorName": "Dr. Smith",
  "scheduledAt": "2026-07-15T10:00:00"
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "patientName": "John Doe",
  "patientEmail": "john.doe@example.com",
  "doctorName": "Dr. Smith",
  "scheduledAt": "2026-07-15T10:00:00",
  "status": "SCHEDULED",
  "createdAt": "2026-06-16T12:00:00"
}
```

### Get Appointment by ID

```http
GET http://localhost:8081/api/appointments/1
```

### List Appointments by Email

```http
GET http://localhost:8081/api/appointments?email=john.doe@example.com
```

## Key Design Decisions

- **`@Transactional`** on `scheduleAppointment` ensures the appointment is only persisted if the database transaction succeeds before the event is published.
- **Java Records** are used for all DTOs and events — immutable, concise, and serialization-friendly.
- **Topic Exchange** in RabbitMQ allows future routing flexibility (e.g., `appointment.cancelled`, `appointment.completed`).
