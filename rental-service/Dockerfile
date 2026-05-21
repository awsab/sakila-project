# ─── Stage 1: Build ───────────────────────────────────────────────────────────
# Full build environment with Maven and JDK 25.
FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /workspace

# Copy complete monorepo so parent/framework modules build correctly.
COPY . .

# Build rental-service only; -am ensures upstream modules are built first.
RUN mvn -f rental-service/pom.xml -pl rental-service -am -DskipTests clean package

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
# JRE-only runtime to keep the image small and secure (no JDK tools exposed).
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the self-contained Spring Boot fat-jar.
COPY --from=build /workspace/rental-service/target/rental-service-0.0.1-SNAPSHOT.jar app.jar

# --- Sensible defaults (runtime-overrideable; no secrets here) ---
# Default server port matching application.yaml.
ENV SERVER_PORT=8082
# Default to SIT profile; overridden by compose or k8s for other environments.
ENV SPRING_PROFILES_ACTIVE=sit
# Default Eureka endpoint for standalone `docker run`; compose uses container DNS.
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/

# EXPOSE is documentation only — does not publish the port to the host.
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]

