# =========================================================================
# Stage 1: Build the Java application using Maven
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the build configuration file and source code
COPY pom.xml .
COPY app/src ./app/src

# Package the application into a compiled JAR file, bypassing unit tests to save deployment time
RUN mvn clean package -DskipTests

# =========================================================================
# Stage 2: Create the lightweight runtime environment
# =========================================================================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the compiled executable JAR file from the build stage
# Note: Adjust the JAR filename below if your pom.xml yields a different name
COPY --from=build /app/target/*.jar app.jar

# Expose the network port the application listens on (typically 8080 for web applications)
EXPOSE 8080

# Environment variables to handle fail-safe container startup behavior
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

# Command to execute the application when the container spins up
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
