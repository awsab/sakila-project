# ─── Stage 1: Build ───────────────────────────────────────────────────────────
# Maven + JDK 25 build stage.
FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /workspace

# Full monorepo copy required for shared parent/framework resolution.
COPY . .

# Build inventory-service; -am builds parent + framework first.
RUN mvn -f inventory-service/pom.xml -pl inventory-service -am -DskipTests clean package

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
# Slim JRE runtime image.
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the packaged Spring Boot fat-jar.
COPY --from=build /workspace/inventory-service/target/inventory-service-0.0.1-SNAPSHOT.jar app.jar

# --- Sensible defaults (overrideable; no secrets hardcoded) ---
# Inventory application.yaml has no port configured — 8083 matches what compose assigns.
ENV SERVER_PORT=8083
# Default to SIT; compose/k8s overrides for UAT/PROD.
ENV SPRING_PROFILES_ACTIVE=sit
# Eureka URL default for bare docker run; compose overrides to container DNS name.
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/

# EXPOSE documents intended port — does not publish to host.
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]

