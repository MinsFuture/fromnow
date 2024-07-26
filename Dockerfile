# Base image
FROM --platform=linux/amd64 openjdk:17-jdk

# Argument for the JAR file location
ARG JAR_FILE=build/libs/fromnow-0.0.1-SNAPSHOT.jar

# Copy the JAR file to the container
COPY ${JAR_FILE} app.jar

COPY src/main/resources/sunny-wavelet-429609-t9-5d820b98637e.json /app/resources/sunny-wavelet-429609-t9-5d820b98637e.json

COPY src/main/resources/sunny-wavelet-429609-t9-5d820b98637e.json /src/main/resources/sunny-wavelet-429609-t9-5d820b98637e.json
# Copy the GCP credentials JSON file
EXPOSE 8080

# Entry point to run the application with the 'dev' profile active
ENTRYPOINT ["java", "-jar", "/app.jar"]
