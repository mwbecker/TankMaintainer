# Start with a base image containing Java runtime
FROM eclipse-temurin:17-jdk

# Add the fat jar to the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]