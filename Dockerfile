# Use an official Gradle image as the build stage
FROM gradle:8.6-jdk21 AS builder
WORKDIR /app

# Copy the project files
COPY --chown=gradle:gradle . .

# Build the application, skipping tests
RUN gradle build -x test

# Use a minimal Java 21 runtime for the final image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port (change as needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]