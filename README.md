# Incode Home Task

Three independently runnable Spring Boot services for company searches and verification retrieval. The backend service calls free and premium third-party providers over HTTP; neither provider is a dependency of the backend artifact.

## Service topology

| Service | Default port | Responsibilities |
| --- | --- | --- |
| `backend-service` | 8080 | Search orchestration, free-to-premium fallback, verification persistence, authentication, and observability |
| `free-service` | 8081 | `GET /free-third-party`; simulates unavailability 40% of the time |
| `premium-service` | 8082 | `GET /premium-third-party`; simulates unavailability 10% of the time |

The backend calls `http://localhost:8081` and `http://localhost:8082` by default. Override these addresses with `FREE_PROVIDER_BASE_URL` and `PREMIUM_PROVIDER_BASE_URL`.

## Prerequisites

- Java 25
- PostgreSQL 16+ only when using the backend's `database` profile

The Gradle wrapper is included, so a separate Gradle installation is not required.

## Run the services

Start each provider in a separate terminal:

```bash
./gradlew :free-service:bootRun
./gradlew :premium-service:bootRun
```

Then start the backend at `http://localhost:8080`:

```bash
./gradlew :backend-service:bootRun
```

Each module produces its own executable JAR with `./gradlew bootJar`.

### Run from IntelliJ IDEA

Open the repository root as a Gradle project, sync it, and configure Java 25 as both the Project SDK and Gradle JVM. Create three Spring Boot run configurations:

| Name | Main class | Module classpath | Working directory |
| --- | --- | --- | --- |
| Free Service | `com.marcos.incode_home_task.FreeServiceApplication` | `free-service` | `$PROJECT_DIR$/free-service` |
| Premium Service | `com.marcos.incode_home_task.PremiumServiceApplication` | `premium-service` | `$PROJECT_DIR$/premium-service` |
| Backend Service | `com.marcos.incode_home_task.IncodeHomeTaskApplication` | `backend-service` | `$PROJECT_DIR$/backend-service` |

Start Free Service and Premium Service before Backend Service. The working directories are important because relative paths, including the backend log file, resolve from them. This keeps the active backend log at `backend-service/logs/incode-home-task.log`.

You can create a Compound run configuration containing all three entries for one-click startup. If Spring Boot run configurations are not available in your IntelliJ edition, create standard Application configurations with the same main classes, modules, and working directories.

### Local profile

The backend's `local` profile is the default. It uses an in-memory H2 database, runs Flyway migrations, and disables OTLP trace export.

```bash
./gradlew :backend-service:bootRun
```

To activate it explicitly:

```bash
./gradlew :backend-service:bootRun --args='--spring.profiles.active=local'
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

Create a PostgreSQL database and a role that has access to it, then set the connection details and start the app. The following values match the application's defaults (set the role password to `mypass321` when creating it):

```bash
export DATABASE_URL='jdbc:postgresql://localhost:5432/incode_home_task'
export DATABASE_USERNAME='hometask'
export DATABASE_PASSWORD='mypass321'

./gradlew :backend-service:bootRun --args='--spring.profiles.active=database'
```

`DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` default to `jdbc:postgresql://localhost:5432/incode_home_task`, `hometask`, and `mypass321`, respectively. The configured PostgreSQL JDBC driver, Flyway PostgreSQL support, and `TIMESTAMP WITH TIME ZONE` migration are compatible with PostgreSQL.

### OTLP export

OTLP trace and metric export are disabled in both backend profiles by default. The Spring Boot 4.1 configuration names used by the backend are current:

- `management.tracing.export.otlp.enabled` for trace-export enablement
- `management.otlp.metrics.export.enabled` for metrics-export enablement

Enable the exporters only when an OTLP collector is available, and provide its HTTP endpoints. For a collector listening on the conventional OTLP/HTTP port, for example:

```bash
export OTLP_TRACING_ENABLED=true
export MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT='http://localhost:4318/v1/traces'

export OTLP_METRICS_ENABLED=true
export MANAGEMENT_OTLP_METRICS_EXPORT_URL='http://localhost:4318/v1/metrics'
```

The tracing exporter uses `management.opentelemetry.tracing.export.otlp.endpoint`; the metrics exporter uses `management.otlp.metrics.export.url`. Prometheus scraping at `/actuator/prometheus` remains available independently of OTLP metrics export.

## Swagger and OpenAPI

After starting a service, open its Swagger UI at:

```text
http://localhost:8080/swagger-ui.html
http://localhost:8081/swagger-ui.html
http://localhost:8082/swagger-ui.html
```

The generated OpenAPI documents are also available at:

```text
http://localhost:8080/v3/api-docs
http://localhost:8080/v3/api-docs.yaml
```

## Actuator and metrics

The backend exposes the following Actuator endpoints:

| Endpoint | Purpose |
| --- | --- |
| `GET /actuator/health` | Application health status |
| `GET /actuator/info` | Application information |
| `GET /actuator/metrics` | Available metric names |
| `GET /actuator/metrics/{metricName}` | Details for one metric |
| `GET /actuator/prometheus` | Prometheus scrape-format metrics |
| `GET /actuator/logfile` | Current application log file |
| `GET /actuator/loggers` | Logger levels and logger configuration |

Examples:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/metrics/backend.search.calls
curl http://localhost:8080/actuator/prometheus
curl http://localhost:8080/actuator/logfile
curl http://localhost:8080/actuator/loggers
```

`/actuator/prometheus` is the endpoint a Prometheus-compatible collector should scrape. It includes JVM, HTTP server, cache, and custom application metrics such as `backend.search.calls`, `backend.search.duration`, and third-party-provider counters.

Backend application logs are written to `logs/incode-home-task.log` as well as the console. The log file is excluded from Git and can be viewed in a browser at `http://localhost:8080/actuator/logfile`.

`/actuator/loggers` shows the active logger levels. It can also change a logger level at runtime; do not expose this endpoint publicly without authentication.

## Verification endpoint authentication

Only `GET /verifications` requires HTTP Basic authentication. All other API endpoints, including `GET /verifications/{verificationId}`, remain public.

The local defaults are:

```text
Username: verification-reader
Password: changeit
```

Override them with `VERIFICATIONS_USERNAME` and `VERIFICATIONS_PASSWORD` before starting the application. For example:

```bash
curl -u verification-reader:changeit http://localhost:8080/verifications
```

## Tests

Run the complete test suite with:

```bash
./gradlew test
```
