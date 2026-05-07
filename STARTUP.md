# Study Group Platform - Startup Guide

## Prerequisites
- Docker Desktop running
- Docker Compose v2+

## First-time / Clean Start

```bash
# Always use --no-cache on first run or after code changes
docker compose down -v
docker compose build --no-cache
docker compose up
```

## Subsequent Starts (no code changes)

```bash
docker compose down -v
docker compose up
```

## Services
| Service | Port | URL |
|---------|------|-----|
| Frontend | 3000 | http://localhost:3000 |
| API Gateway | 8080 | http://localhost:8080 |
| Auth Service | 8081 | http://localhost:8081 |
| User Service | 8082 | http://localhost:8082 |
| Group Service | 8083 | http://localhost:8083 |
| Discussion Service | 8084 | http://localhost:8084 |
| Kafka UI | 8090 | http://localhost:8090 |

## Notes
- `docker compose down -v` clears database volumes (required for clean migrations)
- JWT_SECRET is set in the `.env` file — change it for production
