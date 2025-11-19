# --- Stage 1: Build ---
# Use a Maven image with a JDK to build your application
FROM maven:3.9.0-eclipse-temurin-17-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the JAR file. The -DskipTests is optional but speeds up the build.
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime ---
# Use a minimal JRE image for the final production image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the generated JAR file from the 'build' stage
# Adjust 'app-name-0.0.1-SNAPSHOT.jar' to match your actual Maven artifact name
COPY --from=build /app/target/*.jar app.jar

# Spring Boot defaults to port 8080
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]