# Stage 1: Build the project
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and Maven wrapper
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw

# Copy source code
COPY src src

# Build the jar
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the Spring Boot app with Render PORT
ENTRYPOINT ["java","-jar","app.jar","--server.port=${PORT}"]
