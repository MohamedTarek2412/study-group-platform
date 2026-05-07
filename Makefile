.PHONY: up down build logs ps clean restart infra

# Start everything
up:
	docker compose up -d

# Start only infrastructure (DBs + Kafka)
infra:
	docker compose up -d zookeeper kafka postgres-auth postgres-user postgres-group postgres-discussion

# Stop everything
down:
	docker compose down

# Stop and remove volumes (full reset)
clean:
	docker compose down -v --remove-orphans

# Rebuild all images
build:
	docker compose build --no-cache

# Follow logs
logs:
	docker compose logs -f

# Service-specific logs
logs-%:
	docker compose logs -f $*

# Status
ps:
	docker compose ps

# Restart a service: make restart svc=auth-service
restart:
	docker compose restart $(svc)