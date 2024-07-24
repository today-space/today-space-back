# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Install necessary tools
RUN apt-get update && apt-get install -y git wget unzip

# Set the working directory in the container
WORKDIR /app

# Copy the build.gradle, settings.gradle and Gradle wrapper
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy the source code
COPY src ./src

# Build the project
RUN ./gradlew clean build

# Copy the project’s build artifact (JAR file) to the container
COPY build/libs/*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]