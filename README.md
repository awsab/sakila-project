A high-performance, cloud-native implementation of the Sakila DVD rental system. This project demonstrates modern distributed systems patterns using Spring Boot 4, Java 25 (Virtual Threads), and a robust Observability stack.

🏗 Architecture Overview
The system is decomposed into domain-driven microservices, leveraging the Sakila schema:

Inventory Service: Manages films, actors, and stock levels.

Customer Service: Handles user profiles and store memberships.

Payment Service: Processes rentals and financial transactions.

🛠 Tech Stack
Runtime: Java 25 (utilizing latest JEPs and Project Loom)

Framework: Spring Boot 4.0 (Jakarta EE 11+)

Data Layer: Spring Data JPA, Hibernate, PostgreSQL

Code Generation: * Lombok: Reduces boilerplate.

MapStruct: Type-safe bean mapping.

JPAModelgen: Static metamodel for type-safe Criteria queries.

Observability: Micrometer, OpenTelemetry, Prometheus, and Grafana.

Testing: JUnit 5, Mockito, and Testcontainers for ephemeral database testing.

Infrastructure: Docker, Docker Compose.
