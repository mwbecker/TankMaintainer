# Use a more generic Maven base image that supports both ARM and x86 architectures
FROM maven:3.8-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy your Maven project files and install dependencies
COPY pom.xml .
COPY src ./src

# Run Maven build (skip tests for now)
RUN mvn clean package -DskipTests

# Second stage: use a slimmer JDK image to run the app
FROM maven:3.8-openjdk-17

# Copy the JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java -jar /app.jar --server.port=$PORT"]