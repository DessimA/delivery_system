FROM openjdk:22-jdk-slim

WORKDIR /app

COPY mvnw ./mvnw
COPY mvnw.cmd ./mvnw.cmd
COPY .mvn ./.mvn
COPY pom.xml ./pom.xml
COPY src ./src

RUN chmod +x ./mvnw
RUN apt-get update && apt-get install -y dos2unix
RUN dos2unix ./mvnw
RUN ls -la /app
RUN cat ./mvnw
RUN bash ./mvnw clean install -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/Delivery-0.0.1-SNAPSHOT.jar"]