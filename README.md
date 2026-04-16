# Loan Approval Process

A robust backend REST application that implements a loan approval process. The system receives loan applications, validates customer criteria ( maximum age limitation derived from an Estonian personal code), generates an annuity-based payment schedule, and manages the application status lifecycle using a PostgreSQL database.

## Technologies Used
* **Java 21**
* **Spring Boot 3** (Web, Data JPA, Validation)
* **PostgreSQL** (Database)
* **Flyway** (Database schema migrations)
* **OpenAPI 3 / Swagger** (API Documentation)
* **Docker & Docker-Compose** (Containerization)
* **JUnit 5 & Mockito** (Unit Testing)

## Core Business Logic
* Parses the Estonian identification code (`isikukood`) to calculate customer age.
* Rejects applications if the customer exceeds the age limit.
* Generates an exact monthly annuity payment schedule.
* Assures zero remaining balance at the end of the loan term.
* Strict state machine for Loan status (`IN_REVIEW` -> `APPROVED` / `REJECTED`).

## Quick Start (Dockerized)

The simplest way to run the application and database together is by using Docker Compose.

1. Clone or download the repository.
2. Open a terminal in the root directory of the project (where `docker-compose.yaml` is located).
3. Run the following command:

```bash
docker-compose up --build
```
*(Optionally add `-d` at the end to run it in the background)*

This will automatically:
* Build the Spring Boot application container.
* Pull and start a PostgreSQL 15 database.
* Execute Flyway database schema migrations.
* Start the server on port `8080`.

## API Documentation

Once the application is running, you can interact with the API endpoints entirely through the Swagger UI dashboard.

Open your browser and navigate to:
**http://localhost:8080/swagger-ui.html**

## Running Tests

Automated tests are isolated and use Mockito to simulate database interactions. They run without requiring the Docker environment. 

To execute the unit tests via Maven wrapper:

```bash
./mvnw test
```
(Or run them directly via your IDE, IntelliJ IDEA).

## Configuration

Standard application properties are located in `src/main/resources/application.properties`.

* **loan.age-limit**: Configures the maximum age limit for a loan applicant (Default: 70).

