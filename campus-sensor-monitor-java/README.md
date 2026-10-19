
# Campus Sensor Monitoring and Alerts

A REST API built with Java and Spring Boot to ingest campus sensor readings, evaluate configurable alert rules, and manage alerts generated when readings cross defined thresholds. PostgreSQL is used for persistent storage.

## 1. Features

- Ingest individual sensor readings or batches of readings.
- Validate incoming readings, including event IDs, sensor IDs, metric types, values, and timestamps.
- Configure, list, enable, and disable alert rules.
- Evaluate readings against configured conditions and thresholds.
- Retrieve alerts and filter them by status.
- Acknowledge and resolve alerts.
- Check service health.
- Load-test the API using Grafana k6.

## 2. Technology Stack

| Component | Technology |
|---|---|
| Language | Java |
| Backend framework | Spring Boot 3.4.5 |
| REST server | Spring MVC with embedded Tomcat |
| Persistence | Spring Data JPA and Hibernate |
| Database | PostgreSQL |
| Database connection pool | HikariCP |
| Build tool | Apache Maven |
| Load testing | Grafana k6 |

## 3. Prerequisites

Install the following software before running the application:

1. **Java JDK 21 or compatible newer JDK.** The project is configured for Java 21 compatibility and has also been run locally using Java 27.
2. **Apache Maven 3.9 or newer.**
3. **PostgreSQL** running locally or on a reachable database server.
4. **Grafana k6**, required only for load testing.

Verify Java and Maven in PowerShell:

```powershell
java -version
mvn -version
```

Verify k6 if you intend to run the performance tests:

```powershell
k6 version
```

## 4. Installation and Configuration

# Campus Sensor Monitoring and Alerts

## Running the Project

### 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd <YOUR_PROJECT_FOLDER>
```

Alternatively, download the repository as a ZIP file from GitHub and extract it.

### 2. Configure the Database

Create a PostgreSQL database and open:

`src/main/resources/application.properties`

Update the database URL, username, and password according to your local PostgreSQL configuration.

Do not commit real database credentials to GitHub.

### 3. Start the Application

Ensure PostgreSQL is running, then execute the following command from the project root:

```bash
mvn spring-boot:run
```

Wait for Spring Boot to start successfully. The API will be available at:

`http://localhost:8080`

### 4. Check Service Health

Open another terminal and run:

```bash
curl http://localhost:8080/health
```

A successful response indicates that the health endpoint is accessible.

## Testing the API

### 5. Create an Alert Rule

Send a `POST` request to `/api/rules` with the following JSON body:

```json
{
  "name": "High temperature alert",
  "sensorId": "sensor-001",
  "metricType": "temperature",
  "condition": "GT",
  "threshold": 35,
  "windowSeconds": 0,
  "enabled": true
}
```

### 6. Submit a Sensor Reading

Send a `POST` request to `/api/readings` with this JSON body:

```json
{
  "eventId": "7a7e2142-9ef0-48da-9ca7-6d0876b74719",
  "sensorId": "sensor-001",
  "type": "temperature",
  "value": 38.5,
  "timestamp": "2026-10-19T10:30:00Z"
}
```

Use a valid, unique UUID for each new event. This reading exceeds the configured temperature threshold of 35 and should trigger an alert if the rule is enabled and matches.

### 7. Retrieve Alerts

Retrieve all alerts:

```text
GET http://localhost:8080/api/alerts
```

Retrieve only open alerts:

```text
GET http://localhost:8080/api/alerts?status=OPEN
```

## Available API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/health` | Check service health |
| POST | `/api/readings` | Submit a sensor reading |
| POST | `/api/readings/batch` | Submit multiple readings |
| POST | `/api/rules` | Create an alert rule |
| GET | `/api/rules` | Retrieve alert rules |
| PUT | `/api/rules/{id}` | Update a rule |
| PATCH | `/api/rules/{id}/enable` | Enable a rule |
| PATCH | `/api/rules/{id}/disable` | Disable a rule |
| GET | `/api/alerts` | Retrieve alerts |
| PATCH | `/api/alerts/{id}/acknowledge` | Acknowledge an alert |
| PATCH | `/api/alerts/{id}/resolve` | Resolve an alert |

## Important Notes

- Keep PostgreSQL and the Spring Boot application running while using the API.
- Configure database credentials for your own environment.
- Execute commands from the project root unless specified otherwise.
- Replace the example GitHub repository URL and project folder name with the appropriate values.
## 5. Running the Service

Start the application from the project root:

```powershell
mvn spring-boot:run
```

Wait for Spring Boot to finish starting. The service runs on port `8080` by default.

Open another PowerShell terminal and check the health endpoint:

```powershell
Invoke-RestMethod http://localhost:8080/health
```

A successful response indicates that the health endpoint is reachable. For database-dependent operations, ensure PostgreSQL is also available and the datasource credentials are correct.

To stop the application, press `Ctrl + C` in the terminal running Spring Boot.

## 6. API Endpoints

The following endpoints are provided by the application.

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/health` | Check service health |
| POST | `/api/readings` | Submit an individual reading |
| POST | `/api/readings/batch` | Submit a batch of readings |
| POST | `/api/rules` | Create an alert rule |
| GET | `/api/rules` | List alert rules |
| PUT | `/api/rules/{id}` | Update a rule |
| PATCH | `/api/rules/{id}/enable` | Enable a rule |
| PATCH | `/api/rules/{id}/disable` | Disable a rule |
| GET | `/api/alerts` | Retrieve alerts |
| GET | `/api/alerts?status=OPEN` | Retrieve open alerts |
| PATCH | `/api/alerts/{id}/acknowledge` | Acknowledge an alert |
| PATCH | `/api/alerts/{id}/resolve` | Resolve an alert |

Use the actual response schemas defined by the application when integrating other clients.

### Example: Create an alert rule

Send a `POST` request to `/api/rules` with the JSON body:

```json
{
  "name": "High library temperature",
  "sensorId": "library-floor-2",
  "metricType": "temperature",
  "condition": "GT",
  "threshold": 35,
  "windowSeconds": 0,
  "enabled": true
}
```

This example defines a rule that triggers when a matching temperature reading exceeds 35.

### Example: Submit a sensor reading

Send a `POST` request to `/api/readings` with:

```json
{
  "eventId": "7a7e2142-9ef0-48da-9ca7-6d0876b74719",
  "sensorId": "library-floor-2",
  "type": "temperature",
  "value": 36.5,
  "timestamp": "2026-10-19T10:30:00Z"
}
```

Use a new valid UUID for each unique event. The timestamp must be a valid ISO-8601 instant.

If the reading matches an enabled rule, the application evaluates the rule and may create an alert.

## 7. Architecture

The application follows a layered backend architecture.

```text
Sensor Clients / Load Generator
              |
              v
       REST Controllers
              |
              v
     Request DTO Validation
              |
              v
       Service Layer
        /         \
       v           v
 Reading and     Alert Rule
 Persistence     Evaluation
       \           /
        v         v
       Spring Data JPA
              |
              v
          PostgreSQL
```

### REST Controller Layer

The controllers expose HTTP endpoints for reading ingestion, rule management, alert retrieval, and alert lifecycle operations. They accept requests and return API responses.

### DTO and Validation Layer

Request DTOs define the API's input structure and validate fields before normal service processing. Examples include UUID event identifiers, non-empty sensor IDs, metric types, numeric bounds, timestamps, and the maximum batch size.

### Service Layer

The service layer coordinates ingestion and alert processing. It applies the application's rule-evaluation behavior and handles the operations needed to create and manage alerts.

### Persistence Layer

Spring Data JPA and Hibernate map application entities to PostgreSQL tables. HikariCP manages database connections used by the application.

### Database Layer

PostgreSQL stores application data so that readings, rules, and alerts can persist beyond an individual HTTP request.

## 8. Throughput and Reliability Trade-offs

### Batch ingestion

Batch requests reduce HTTP overhead compared with sending one request per reading. However, large request bodies require more memory and may take longer to validate, evaluate, and persist.

A batch limit of 10,000 permits large requests but does not guarantee that they will complete within one second.

### Database writes

The persistence strategy can be a major throughput bottleneck. Repeated database operations, transaction overhead, and rule lookups can increase processing time.

Potential optimizations include JDBC batch inserts, PostgreSQL bulk ingestion where compatible with the application's behavior, suitable database indexes, and reducing repeated rule queries. These are optimization options and should not be treated as implemented unless verified in the source code.

### Alert correctness

Alert generation must remain consistent with the configured rules. Performance improvements must preserve validation, threshold comparisons, event deduplication where implemented, and alert lifecycle behavior.

### Concurrency and backpressure

Increasing concurrent requests can improve utilization, but it can also exhaust database connections, increase lock contention, and raise latency. Bounded concurrency, sensible batch sizes, and monitoring are important when scaling ingestion.

### Reliability limitations

Successful HTTP responses demonstrate that requests were accepted by the API under the tested conditions. They do not, by themselves, prove resilience to database outages, sustained overload, process crashes, or duplicate events.

A production deployment would need additional monitoring, capacity testing, failure recovery, and operational safeguards.

## 9. Load Testing

Grafana k6 is used to measure request duration, failed requests, and accepted-reading counts.

### Install k6

On Windows, if it is not already installed:

```powershell
winget install k6 --source winget
```

Open a new PowerShell terminal and verify the installation:

```powershell
k6 version
```

### Test A: 10,000 readings in one batch

With the Spring Boot service running, execute:

```powershell
k6 run --summary-export .\single-batch-summary.json .\scripts\load-test.js
```

The script should be configured for one iteration containing 10,000 readings.

### Test B: 10,000 readings scheduled within one second

Use the separate burst-test script:

```powershell
k6 run --summary-export .\burst-10000-summary.json .\scripts\burst-10000.js
```

The burst script should schedule 10 batch requests of 1,000 readings each during a one-second arrival window. This measures the ability to handle a burst; requests may finish after the scheduling window.

The JSON summary files are separate artifacts and should be retained alongside the report.

### Observed single-batch test

The local single-batch test produced the following results:

| Metric | Observed result |
|---|---:|
| Readings accepted | 10,000 |
| HTTP requests | 1 |
| Failed HTTP requests | 0 |
| Successful checks | 100% |
| Request duration | Approximately 1 minute 42 seconds |

These measurements are from the local test environment and are not a general performance guarantee.

### Interpretation

The single-batch test confirms that the API accepted 10,000 readings in one request under the tested conditions. The approximately 102-second request duration indicates that this large batch is not processed within a one-second target.

A separate one-second burst test must complete successfully before claiming that the service can handle 10,000 readings within one second. Record its actual accepted-reading count, failed requests, and latency after the test has completed. Do not substitute scheduled arrival rate for measured processing throughput.

## 10. Troubleshooting

**Application fails during startup**

- Confirm that PostgreSQL is running.
- Check the database URL, username, password, and database name in `application.properties`.
- Review the first `Caused by:` message in the startup log.

**PowerShell does not recognize `mvn` or `k6`**

- Verify the software is installed.
- Open a new terminal after installation.
- Confirm the installation directory is included in PATH.

**The API returns HTTP 400**

- Check that the JSON body matches the request DTO.
- Ensure each `eventId` is a valid UUID.
- Check required fields, timestamp format, numeric bounds, and batch-size limits.

**The burst test fails**

- Check that Spring Boot and PostgreSQL remain running.
- Inspect the response body and the k6 error output.
- Confirm the load-test script uses the intended request size and valid event IDs.
- Do not treat a script crash or rejected request as a performance result.

## 11. Future Improvements

- Optimize high-volume database writes.
- Reduce repeated database lookups during rule evaluation.
- Benchmark different batch sizes and concurrency levels.
- Add sustained-load and failure-recovery tests.
- Monitor CPU, memory, database connections, request latency, and error rates.
- Consider asynchronous ingestion or a message queue if the expected workload justifies the added complexity.

## 12. Conclusion

Campus Sensor Monitoring and Alerts provides a Java Spring Boot REST API for ingesting campus sensor readings, managing alert rules, and tracking alerts using PostgreSQL.

The load-test results show that the service accepted a batch of 10,000 readings, while the measured request duration identifies an opportunity for optimization. Further testing is necessary before claiming a sustained capacity of 10,000 readings per second or successful processing of a 10,000-reading burst within one second.
