# CivicReport - Civic Reporting Portal

## Project Overview

CivicReport is a full-stack civic complaint management system.

- Citizens can submit complaints and receive a tracking ID.
- Citizens can track complaint status using the tracking ID.
- Zone admins can log in, view filtered complaint queues, and update department, priority, status, and notes.

The repository also contains an older Java Swing prototype under `src/`, which is kept untouched for reference.

## Tech Stack

- Frontend: React (Vite), plain CSS
- Backend: Java 21, Spring Boot 3.x, Spring Web, Spring Data JPA, Spring Validation
- Database: H2 in-memory for development (MySQL-compatible JPA model)
- Build tools: Maven (backend), npm + Vite (frontend)

## Project Structure

```text
frontend/
  src/
    components/
    pages/
    services/
    hooks/
    utils/
backend/
  src/main/java/com/civicreport/
    controller/
    service/
    repository/
    model/
    dto/
    exception/
    config/
src/   (legacy Swing prototype - untouched)
```

## Backend Setup

1. Open terminal in project root.
2. Run backend:

```powershell
cd backend
..\tools\apache-maven-3.9.14\bin\mvn.cmd spring-boot:run
```

Backend runs on `http://localhost:8080`.

### H2 Console (Development)

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:civicreportdb`
- Username: `sa`
- Password: *(empty)*

## Frontend Setup

1. Create environment file from example:

```powershell
cd frontend
Copy-Item .env.example .env -Force
```

2. Install and run:

```powershell
cmd /c npm install
cmd /c npm run dev
```

Frontend runs on `http://localhost:5173`.

## Run Both Together

1. Start backend in one terminal.
2. Start frontend in another terminal.
3. Open `http://localhost:5173`.

## API Endpoints

### Complaint Endpoints

- `POST /api/complaints`
  - Create complaint
  - Request fields: `reporterName`, `reporterEmail` (optional), `zone`, `issueName`, `description`, `locationAddress`, `imageData` (optional base64/image URL string)
  - Response: `{ "trackingId": "...", "message": "..." }`
- `GET /api/complaints/{trackingId}`
  - Public complaint lookup by tracking ID
- `GET /api/complaints?zone=&status=&priority=`
  - Admin-only complaint listing (requires Bearer token)
- `PATCH /api/complaints/{id}`
  - Admin-only update (department, priority, status, note)

### Admin Endpoints

- `POST /api/admin/login`
  - Request: `{ "zoneId": "...", "adminId": "...", "password": "..." }`
  - Response: `{ "token": "...", "adminName": "...", "zone": "..." }`
- `GET /api/admin/stats/{zone}`
  - Admin-only zone metrics
  - Response: `{ "total": 0, "pending": 0, "resolved": 0, "highPriority": 0 }`

## Error Response Format

All API errors follow:

```json
{
  "error": "message",
  "status": 400
}
```

## Notes

- CORS is enabled for local frontend/backend integration.
- `application.properties` is configured for dev with H2 and `ddl-auto=create-drop`.
- Admin credentials are seeded on backend startup for development use only.
