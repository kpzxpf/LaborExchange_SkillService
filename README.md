# Skill Service

Central skill dictionary for LaborExchange. Skills are shared across vacancies and resumes.

## Overview

| Property | Value |
|---|---|
| Port | **8086** |
| Base path | `/api/skills` |
| Database | PostgreSQL (`skilldb`, port 5438) |
| Cache | Redis (TTL: 2 hours) |
| Swagger UI | http://localhost:8086/swagger-ui.html |
| Prometheus metrics | http://localhost:8086/actuator/prometheus |

## API Endpoints

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/skills` | Create skill (idempotent — returns existing if name matches) |
| `PUT` | `/api/skills/{id}` | Update skill |
| `GET` | `/api/skills/{id}` | Get skill by ID |
| `GET` | `/api/skills` | Get all skills |
| `GET` | `/api/skills/names/by-ids?ids=` | Get skill names by IDs (bulk, internal) |
| `DELETE` | `/api/skills/{id}` | Delete skill |

## Caching Strategy (Redis)

| Cache | TTL | Eviction |
|---|---|---|
| `skills:all` | 2 hours | On create, update, delete |
| `skills` (per ID) | 2 hours | On update, delete |
| `skills:ids` (bulk) | 2 hours | On update, delete |

## Pre-loaded Skills

53 skills are pre-loaded via Flyway migration (V2), covering:
- **Backend:** Java, Spring Boot, Python, Go, Node.js, C++, Rust...
- **Frontend:** React, Vue.js, Angular, TypeScript, CSS...
- **DevOps:** Docker, Kubernetes, AWS, CI/CD, Terraform...
- **Data:** SQL, PostgreSQL, MongoDB, Redis, Elasticsearch, Kafka...
- **Other:** Git, REST API, GraphQL, Agile, Security...

## Running locally

```bash
./gradlew bootRun
```

Requires: PostgreSQL (port 5438), Redis.

## Error Responses

```json
{
  "error": "Skill not found",
  "code": 404,
  "timestamp": "2026-03-20T12:00:00"
}
```
