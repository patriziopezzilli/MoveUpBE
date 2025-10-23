# Use Eclipse Temurin JDK 17 for building (includes Maven)
FROM eclipse-temurin:17-jdk AS build

# Install Maven if not included
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use OpenJDK 17 JRE for runtime (Eclipse Temurin is more stable)
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (default for Spring Boot)
EXPOSE 8080

# Run the application
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]