FROM openjdk:17-alpine

WORKDIR /app

COPY mvnw ./mvnw
COPY mvnw.cmd ./mvnw.cmd
COPY .mvn ./.mvn
COPY pom.xml ./pom.xml
COPY src ./src

RUN chmod +x ./mvnw
RUN apk add dos2unix
RUN dos2unix ./mvnw
RUN ls -la /app
RUN cat ./mvnw
RUN sh ./mvnw clean install -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/Delivery-0.0.1-SNAPSHOT.jar"]