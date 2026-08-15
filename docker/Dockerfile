# ==============================================================================
# Multi-Stage Dockerfile for CUST Enterprise Platform (Java 21 LTS & Linux)
# NIST CSF PR.IP Compliant: Minimal surface, unprivileged user, reproducible build
# ==============================================================================

# Stage 1: Build Java 21 Spring Boot Artifact
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /workspace

# Copy Maven Wrapper & Pom
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build production jar
COPY src src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Hardened Runtime Container
FROM eclipse-temurin:21-jre-jammy
LABEL maintainer="CUST Computer Center <cc@cust.edu.tw>"
LABEL org.opencontainers.image.title="CUST Full-Stack Enterprise Portal"
LABEL org.opencontainers.image.version="1.0.0"

# Create unprivileged system group & user (NIST PR.AC)
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --ingroup appgroup appuser

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /workspace/target/*.jar app.jar
COPY src/main/resources/static /app/static

# Configure security permissions
RUN chown -R appuser:appgroup /app
USER appuser:appgroup

# Java runtime flags optimized for container memory & security
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE="prod"

EXPOSE 8080

# Healthcheck for orchestration
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
