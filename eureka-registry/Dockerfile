# ─── Stage 1: Build ───────────────────────────────────────────────────────────
# Use Maven + JDK 25 to compile and package the module.
FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /workspace

# Copy the whole monorepo so parent and framework modules are resolvable during build.
COPY . .

# Build only eureka-registry; -am builds upstream modules (parent, framework) if needed.
# Skip tests to speed up image build (tests run in CI, not at Docker build time).
RUN mvn -f eureka-registry/pom.xml -pl eureka-registry -am -DskipTests clean package

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
# Use a minimal JRE-only image for the final container (no build tool, smaller image).
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the executable fat-jar produced by Spring Boot Maven Plugin.
COPY --from=build /workspace/eureka-registry/target/eureka-registry-0.0.1-SNAPSHOT.jar app.jar

# --- Sensible defaults (runtime config — NOT environment-specific secrets) ---
# These allow the image to work with a plain `docker run` without any flags.
# docker-compose / Kubernetes will override these at deploy-time when needed.

# Default server port — overrideable via -e SERVER_PORT=xxxx at runtime.
ENV SERVER_PORT=8761
# Do not register this server-side registry with itself.
ENV EUREKA_CLIENT_REGISTER_WITH_EUREKA=false
# Do not fetch the registry from itself.
ENV EUREKA_CLIENT_FETCH_REGISTRY=false

# EXPOSE is metadata only — it documents the port but does NOT publish it.
# Actual host-to-container port mapping is always done in docker-compose or `docker run -p`.
EXPOSE 8761

# Launch the Spring Boot application.
ENTRYPOINT ["java", "-jar", "app.jar"]

