# Skill Service

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.3-brightgreen?logo=springboot)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Port](https://img.shields.io/badge/port-8086-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-skilldb-336791?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-cached-DC382D?logo=redis)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

Central skill dictionary microservice. Provides a shared, deduplicated skill catalog used by both vacancies and resumes.

## Table of Contents

- [Overview](#overview)
- [API Endpoints](#api-endpoints)
- [Data Model](#data-model)
- [Caching](#caching)
- [Configuration](#configuration)
- [Running Locally](#running-locally)

## Overview

| Property | Value |
|---|---|
| Port | **8086** |
| Base path | `/api/skills` |
| Database | PostgreSQL — `skilldb` (port 5438) |
| Cache | Redis (TTL: 2 hours) |
| Migrations | Flyway (53 skills pre-loaded) |
| Swagger UI | `http://localhost:8086/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8086/v3/api-docs` |
| Prometheus | `http://localhost:8086/actuator/prometheus` |

## API Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/` | No | Get all skills (cached) |
| `GET` | `/{id}` | No | Get skill by ID |
| `GET` | `/names/by-ids?ids=` | No | Bulk get skill names by IDs |
| `POST` | `/` | JWT | Create a skill (idempotent) |
| `PUT` | `/{id}` | JWT | Update a skill |
| `DELETE` | `/{id}` | JWT | Delete a skill |

### POST /api/skills — idempotency

If a skill with the same name already exists (case-insensitive), the existing record is returned with `201 Created` rather than creating a duplicate.

### GET /api/skills/names/by-ids

**Query parameter:** `ids` — `ids=1&ids=2&ids=5` (Set\<Long\>)

**Response:**
```json
["Java", "Spring Boot", "Kotlin"]
```

Used internally by VacancyService and ResumeService when publishing `VacancyIndexEvent` and `ResumeIndexEvent` to Kafka.

## Data Model

### SkillDto

| Field | Type | Constraints |
|---|---|---|
| `id` | Long | Auto-generated, null on create |
| `name` | String | Required, unique (case-insensitive), max 100 chars |

## Caching

All read operations are cached in Redis with a **2-hour TTL**. Cache is evicted when a skill is created, updated, or deleted.

## Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `8086` | HTTP port |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5438/skilldb` | Database URL |
| `spring.data.redis.host` | `localhost` | Redis host |
| `spring.data.redis.port` | `6379` | Redis port |

## Running Locally

```bash
./gradlew bootRun
```

Requires PostgreSQL on port 5438 (`skilldb`) and Redis on port 6379.
