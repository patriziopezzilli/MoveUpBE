# Use Maven with OpenJDK 17 for building
FROM maven:3.9.4-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use OpenJDK 17 JRE for runtime
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (default for Spring Boot)
EXPOSE 8080

# Run the application
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]