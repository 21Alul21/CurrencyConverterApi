# Use a base image with JDK
FROM eclipse-temurin:17-jdk-alpine

# Set application JAR name (change if needed)
ARG JAR_FILE=target/*.jar

# Copy the jar file into the image
COPY ${JAR_FILE} app.jar

# Expose port 
EXPOSE 8080

#Run the jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
