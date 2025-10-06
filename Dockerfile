# Use an OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file into the container
COPY target/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
