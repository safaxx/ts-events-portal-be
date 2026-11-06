# --- Stage 1: Build ---
# Using a Temurin 25 Maven build image
FROM maven:3-eclipse-temurin-25-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Build the executable JAR file
RUN mvn clean package -DskipTests


# --- Stage 2: Runtime ---
# Using the Eclipse Temurin 25 JRE Alpine image
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copy the generated JAR file from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
