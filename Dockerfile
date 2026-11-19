# ==============================================================================
# Multi-stage Build for Hospital Spring Boot Backend (AWS ECS Fargate)
# ==============================================================================

# Stage 1: Build & Package
FROM maven:3.8.6-openjdk-8-slim AS builder
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source code and package executable JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal Secure Runtime
FROM openjdk:8-jre-alpine
WORKDIR /app

# Create dedicated non-root user for security (HIPAA principle of least privilege)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy JAR artifact from builder stage
COPY --from=builder /app/target/dental-management-system-0.0.1-SNAPSHOT.jar app.jar

# AWS ECS Fargate & ALB configuration
ENV PORT=8080
EXPOSE 8080

# Container Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget -qO- http://localhost:8080/health || exit 1

# Configure JVM for container cgroup memory limits
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
