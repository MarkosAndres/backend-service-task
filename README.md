# Incode Home Task

Spring Boot application for company searches and verification retrieval. It includes H2 and PostgreSQL profiles, OpenAPI documentation, Actuator endpoints, Prometheus metrics, Caffeine caching, and OpenTelemetry tracing support.

## Prerequisites

- Java 25
- PostgreSQL 16+ only when using the `database` profile

The Gradle wrapper is included, so a separate Gradle installation is not required.

## Run the application

The application listens on `http://localhost:8080`.

### Local profile

The `local` profile is the default. It uses an in-memory H2 database, runs Flyway migrations, and disables OTLP trace export.

```bash
./gradlew bootRun
```

To activate it explicitly:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

The H2 console is available at `http://localhost:8080/h2-console`.

Use the following JDBC URL in the H2 console:

```text
jdbc:h2:mem:incode-home-task;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
```

Username: `sa`  
Password: leave empty

### Database profile

The `database` profile uses PostgreSQL and runs Flyway migrations on startup.

Set the connection details, then start the app:

```bash
export DATABASE_URL='jdbc:postgresql://localhost:5432/incode_home_task'
export DATABASE_USERNAME='postgres'
export DATABASE_PASSWORD='postgres'

./gradlew bootRun --args='--spring.profiles.active=database'
```

The defaults above are used when the environment variables are omitted. To enable OTLP trace export in this profile, set `OTLP_TRACING_ENABLED=true` and configure the appropriate OTLP endpoint for your telemetry backend.

## Swagger and OpenAPI

After starting the application, open Swagger UI at:

```text
http://localhost:8080/swagger-ui.html
```

The generated OpenAPI documents are also available at:

```text
http://localhost:8080/v3/api-docs
http://localhost:8080/v3/api-docs.yaml
```

## Actuator and metrics

The following Actuator endpoints are exposed:

| Endpoint | Purpose |
| --- | --- |
| `GET /actuator/health` | Application health status |
| `GET /actuator/info` | Application information |
| `GET /actuator/metrics` | Available metric names |
| `GET /actuator/metrics/{metricName}` | Details for one metric |
| `GET /actuator/prometheus` | Prometheus scrape-format metrics |

Examples:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/metrics/backend.search.calls
curl http://localhost:8080/actuator/prometheus
```

`/actuator/prometheus` is the endpoint a Prometheus-compatible collector should scrape. It includes JVM, HTTP server, cache, and custom application metrics such as `backend.search.calls`, `backend.search.duration`, and third-party-provider counters.

## Tests

Run the complete test suite with:

```bash
./gradlew test
```
