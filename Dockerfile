# Use OpenJDK 17 as base
FROM eclipse-temurin:21-jdk


# Set working directory inside container
WORKDIR /app

# Copy the built JAR into container
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
