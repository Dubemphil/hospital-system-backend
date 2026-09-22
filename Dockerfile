# Multi-stage Build for Hospital Spring Boot Backend
FROM maven:3.8.6-openjdk-8-slim AS builder
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source and package JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime Image: Lightweight, container-optimized JRE
FROM openjdk:8-jre-alpine
WORKDIR /app

# Run as non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/target/dental-management-system-0.0.1-SNAPSHOT.jar app.jar

# Cloud Run defaults to PORT 8080
ENV PORT=8080
EXPOSE 8080

# Configure JVM for container memory limits
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
