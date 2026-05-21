# ─── Stage 1: Build ───────────────────────────────────────────────────────────
# seucurity1 is a standalone module with spring-boot-starter-parent (no sakila-parent).
# A simple single-module build is enough here.
FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /workspace

# Only the security module folder is needed (no shared parent/framework dependency).
COPY seucurity1/ seucurity1/

# Standard Maven package, skipping tests for image build.
RUN mvn -f seucurity1/pom.xml -DskipTests clean package

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
# Minimal JRE runtime image.
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the packaged fat-jar.
COPY --from=build /workspace/seucurity1/target/seucurity1-0.0.1-SNAPSHOT.jar app.jar

# --- Sensible defaults (overrideable at runtime) ---
# Default 8084 avoids collision with customer-service (8081); compose confirms this.
ENV SERVER_PORT=8084

# EXPOSE is documentation only — does not publish to the host.
EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]

