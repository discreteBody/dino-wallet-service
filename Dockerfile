# --- STAGE 1: BUILD THE APPLICATION ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the project files
COPY . .

# Build the application (skipping tests to save time)
RUN mvn clean package -DskipTests

# --- STAGE 2: RUN THE APPLICATION ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the JAR file from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]