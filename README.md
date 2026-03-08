# DDVS — Digital Document Verification System (Backend)

A government-grade document verification API built with Spring Boot. Allows authorized institutions to issue verifiable digital documents — certificates, licenses, permits — each with a unique verification code and QR code that anyone can use to confirm authenticity.

**Frontend repo:** [ddvs-frontend](https://github.com/yourusername/ddvs-frontend)

---

## What It Does

- Authorized issuers (e.g. Ministry of Education) issue documents to individuals
- Each document gets a structured verification code: `TZ-EDU-82911`
- Anyone can verify a document at `GET /verify/{code}` — no account needed
- Every verification attempt is logged with IP address and result
- Documents can be revoked with a recorded reason and audit trail
- QR codes link directly to the verification endpoint

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security 7 + JWT |
| Database | PostgreSQL |
| ORM | JPA / Hibernate 7 |
| QR Codes | ZXing 3.5.3 |
| Build Tool | Maven |

---

## User Roles

| Role | Permissions |
|---|---|
| `ADMIN` | Full access — manage users, issuers, revoke documents, view logs |
| `ISSUER` | Issue and manage documents |
| `AUDITOR` | View documents and verification logs |
| `PUBLIC` | Verify documents only — no account required |

---

## API Endpoints

### Authentication
```
POST /auth/register
POST /auth/login
```

### Issuers (Admin only)
```
POST   /issuers
GET    /issuers
GET    /issuers/{id}
PUT    /issuers/{id}
DELETE /issuers/{id}
```

### Documents
```
POST /documents               → ADMIN, ISSUER
GET  /documents               → ADMIN, AUDITOR
GET  /documents/{id}          → ADMIN, ISSUER, AUDITOR
GET  /documents/issuer/{id}   → ADMIN, ISSUER, AUDITOR
PUT  /documents/{id}/revoke   → ADMIN, ISSUER
```

### Public Verification (no auth required)
```
GET /verify/{verificationCode}
GET /qr/{verificationCode}
```

### Logs
```
GET /verification-logs    → ADMIN, AUDITOR
```

---

## Verification Code Format

```
TZ-EDU-82911
└─┬─┘└─┬─┘└──┬──┘
Country Sector  Random
```

Sector codes: `EDU`, `TAX`, `LIC`, `PRM`, `IMG`, `GEN`

---

## Document Lifecycle

```
VALID → EXPIRED (auto, on expiration date)
VALID → REVOKED (manual, by admin or issuer)
```

---

## Getting Started

### Prerequisites
- Java 17
- PostgreSQL
- Maven

### Setup

1. Clone the repo:
```bash
git clone https://github.com/yourusername/ddvs.git
cd ddvs
```

2. Create the database:
```sql
CREATE DATABASE ddvs_db;
```

3. Configure `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ddvs_db
spring.datasource.username=postgres
spring.datasource.password=your_password

jwt.secret=your-secret-key
jwt.expiration=86400000
```

4. Run:
```bash
mvn spring-boot:run
```

API runs on `http://localhost:8080`

---

## Example Verification Response

```json
{
  "verificationCode": "TZ-EDU-60828",
  "documentType": "CERTIFICATE",
  "title": "Bachelor of Science in Computer Science",
  "ownerName": "John Doe",
  "issuedBy": "Ministry of Education",
  "issuedDate": "2026-03-08",
  "expirationDate": "2030-01-01",
  "status": "VALID",
  "message": "This document is valid and authentic."
}
```

---

## Project Structure

```
src/main/java/com/ddvs
├── config          # Security and CORS configuration
├── controller      # REST controllers
├── dto             # Request and response DTOs
├── entity          # JPA entities
├── repository      # Spring Data repositories
├── security        # JWT filter and UserDetails
├── service         # Business logic
└── util            # JWT and QR code utilities
```

---

## Key Engineering Concepts Demonstrated

- REST API design with layered architecture
- Stateless JWT authentication
- Role-based access control with method-level security
- Audit trail on all document state changes
- Public verification without authentication
- Auto-expiration logic on document retrieval
- IP address logging for abuse detection
- QR code generation linked to verification endpoint