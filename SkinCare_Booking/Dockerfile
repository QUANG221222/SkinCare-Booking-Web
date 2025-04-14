FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the Maven Wrapper files
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer can be cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy the project source
COPY src ./src
COPY .env .

# Build the application
RUN ./mvnw package -DskipTests

# Copy the JAR to WORKDIR
RUN cp target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]