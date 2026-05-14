# Project Overview

BugTracker system.

Stack:
- Spring Boot 3
- PostgreSQL
- React frontend
- Docker Compose
- Maven

## Architecture Rules

- Controllers call only services
- Services contain business logic
- DAO layer handles DB access
- Use DTOs for API communication
- Never expose entities directly

## Code Style

- Use constructor injection
- Use Lombok where possible
- Prefer ResponseEntity in controllers
- Use Flyway for DB migrations

## Commands

Backend:
```bash
mvn spring-boot:run

mvn test

docker compose up --build

