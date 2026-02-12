# Use a lightweight implementation of Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the build file (target/wallet-service-0.0.1-SNAPSHOT.jar)
# Note: We will build this in a moment
COPY target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]