# Stage 1: Maven build environment
FROM eclipse-temurin:21-jdk-alpine AS maven
WORKDIR /app

# Install Maven. Alpine Linux uses apk.
RUN apk add --no-cache maven

# Copy dependency manifests first for caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy src and resources
COPY src src

# Build the application using Maven
# Skip tests during the build stage, they will be run separately
RUN mvn clean package -DskipTests

# Stage 2: Runtime environment
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy the built JAR from the 'maven' stage
COPY --from=maven /app/target/*.jar app.jar

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=5 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:+ZGenerational", "-Djava.awt.headless=true", "-jar", "app.jar"]