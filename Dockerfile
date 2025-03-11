FROM openjdk:25-ea-4-jdk-oraclelinux9

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the target folder into the container
COPY target/mini1.jar app.jar

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
