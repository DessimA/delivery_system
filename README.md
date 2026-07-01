# Delivery System

[![Java](https://img.shields.io/badge/Java-21-blue?logo=java&logoColor=white)](https://jdk.java.net/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-green?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-purple?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Vue.js](https://img.shields.io/badge/Vue-3-4FC08D?logo=vue.js&logoColor=white)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-required-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)

**Author:** Jose Anderson da Silva Costa
**Repository:** [github.com/DessimA/delivery-system](https://github.com/DessimA/delivery-system)

---

## Quick Start

**Prerequisites:** Docker Engine 24+ and Docker Compose v2 only. No local Java, Maven, Node, or npm required.

```bash
cp .env.example .env
cd infra
docker compose up -d
```

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:5173 | - |
| Backend API | http://localhost:8080 | - |
| Swagger UI | http://localhost:8080/swagger-ui.html | - |
| pgAdmin | http://localhost:5050 | `admin@delivery.com` / `admin` |

Sample users (dev profile only): `admin@fakedata.com` / `123456`

---

## VSCode Dev Container

1. Open the project folder in VSCode
2. `Ctrl+Shift+P` -> "Dev Containers: Reopen in Container"
3. The container provides Java 21, Maven, Node 20, and Docker CLI
4. Run `cd infra && docker compose up -d` inside the container terminal

---

## Environment Variables

All configuration is centralized in `.env` at the project root.
Copy `.env.example` and adjust:

```bash
cp .env.example .env
```

Key variables: `DATABASE_URL`, `JWT_SECRET`, `CORS_ORIGINS`, `VITE_APP_API_BASE_URL`.
See `.env.example` for the full list with descriptions.

---

## Hot Reload

- **Backend:** Edit `.java` files -> `inotifywait` detects changes -> `mvn compile` -> DevTools restarts the app (~1-2s)
- **Frontend:** Edit `.vue`/`.js`/`.css` files -> Vite HMR updates the browser instantly

---

## Project Structure

```
.devcontainer/          # VSCode dev container (Java 21 + Node 20 + Docker)
backend/                # Spring Boot 3.4.1 / Java 21
  src/main/java/
    config/             # Security, WebSocket, DataLoader
    controller/         # REST endpoints
    domain/valueobject/ # Cpf, Email (immutable, self-validating)
    dto/                # Java Records for API contracts
    exception/          # RestExceptionHandler, ApiError
    mapper/             # MapStruct interfaces
    model/              # JPA entities
    repository/         # Spring Data JPA
    service/            # Business logic layer
  src/main/resources/
    application.properties
    application-dev.properties
    application-prod.properties
    db/migration/       # Flyway migrations
frontend/               # Vue 3 / Vite / Pinia
  src/
    components/base/    # BaseButton, BaseInput, BaseModal, LoadingSpinner
    components/features/# Cart, Delivery, Product, Order
    composables/        # useApi, useAuth, useNotifications, useWebSocket
    config/             # env.js (centralized API URL)
    plugins/            # Axios config with interceptors
    router/             # Vue Router with auth guards
    services/           # API service layer (user, product, order, etc.)
    stores/             # Pinia stores (auth, cart)
    views/              # AppLogin, AppRegister, AppHome, AppOrders, etc.
infra/
  docker-compose.yml        # Development (default)
  docker-compose.prod.yml   # Production
  docker/
    backend/                # Dockerfile + Dockerfile.dev
    frontend/               # Dockerfile + Dockerfile.dev
```

---

## Commands

```bash
# Start all services
cd infra && docker compose up -d

# Rebuild and restart a service
docker compose build backend
docker compose up -d backend

# View logs
docker compose logs -f backend

# Run tests
docker compose exec backend mvn test
docker compose exec frontend npm run test

# Full restart
docker compose down && docker compose up -d

# Reset database (deletes all data)
docker compose down -v && docker compose up -d

# Production mode
docker compose -f docker-compose.prod.yml up -d
```

---

## Technology Stack

| Tier | Technology |
|------|-----------|
| Backend | Java 21 (Virtual Threads), Spring Boot 3.4.1, Spring Security, Spring Data JPA |
| Frontend | Vue 3 (Composition API), Vite, Pinia, Axios, BootstrapVueNext |
| Database | PostgreSQL 16, Flyway migrations |
| Real-time | WebSocket, STOMP, SockJS |
| Mapping | MapStruct 1.6.3 |
| Auth | Stateless JWT (Bearer) |
| Storage | Local filesystem (FileStorageService interface, swappable to S3/Cloudinary) |

---

## Documentation

| Document | Description |
|----------|-------------|
| [Architecture](docs/en/ARCHITECTURE.md) | Layered architecture, DDD, data flow |
| [Standards](docs/en/STANDARDS.md) | Naming conventions, DTO strategy, component library |
| [Testing](docs/en/TESTING.md) | Testing pyramid, backend/frontend patterns |
| [Setup Guide](docs/en/SETUP.md) | Detailed Docker setup, commands, architecture diagram |
| [Infrastructure Changelog](docs/changes/infra.md) | Docker/devcontainer/environment changes |
| [Security Fixes](docs/changes/seguranca.md) | SEC audit items and corrections |
| [Payment Fixes](docs/changes/pagamento.md) | Payment flow, race conditions, indexes |
| [Delivery Fixes](docs/changes/entrega.md) | Delivery bugs, exception handling, entity fixes |
| [Test Coverage](docs/changes/testes.md) | Test results and identified gaps |
