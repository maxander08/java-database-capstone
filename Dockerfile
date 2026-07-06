# ==========================================
# STAGE 1: Compilation & Maven Build Process
# ==========================================
# Using an official Maven image backed by OpenJDK 17 to compile the Java source files safely.
# This environment is discarded after the build completes, keeping the final image lean.
FROM maven:3.8.5-openjdk-17 AS build-stage

# Set a structured working directory inside the build container container environment
WORKDIR /app

# Copy the Maven Project Object Model (pom.xml) file first.
# This allows Docker to cache the downloaded dependencies unless the pom.xml changes.
COPY pom.xml .

# Pre-download project dependencies to optimize subsequent local build iteration steps
RUN mvn dependency:go-offline -B

# Copy the entire backend application source code directory tree into the container
COPY src ./src

# Compile and package the Java application into a bootable fat JAR file.
# Unit tests are skipped (-DskipTests) exclusively to expedite build times within CI/CD loops.
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: Secure Application Runtime Environment
# ==========================================
# Using an official lightweight Eclipse Temurin JRE alpine image for the production layer.
# This radically slashes the attack surface and brings the final image footprint down.
FROM eclipse-temurin:17-jre-alpine AS runtime-stage

# Establish a non-root app management directory inside the runtime space
WORKDIR /app

# Copy the compiled executable JAR file artifact from the output folder of the build-stage.
# This isolates our final deployment from raw source code and Maven build overhead.
COPY --from=build-stage /app/target/*.jar clinic-backend-app.jar

# Document the internal container network port exposure target mapping rule.
# This tells developers and cloud routing clusters that our Spring Boot API listens on port 8080.
EXPOSE 8080

# Configure key Spring Boot production environment parameters via standard system variables
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m"

# Define the immutable execution entry point array execution signature.
# Utilizes shell-form variable expansion safely to load Java memory tuning flags.
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar clinic-backend-app.jar"]
