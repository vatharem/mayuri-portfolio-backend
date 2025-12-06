# Step 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests clean package

# Step 2: Run the application
FROM eclipse-temurin:21-jdk
WORKDIR /app

# COPY JAR from build stage (NOT from local target folder)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 10000
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
