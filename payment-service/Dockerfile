FROM maven:3-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml /app/../pom.xml
COPY payment-service/pom.xml ./pom.xml
COPY payment-service/src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=build /app/target/*.jar payment-service.jar
ENTRYPOINT [ "java", "-jar", "payment-service.jar" ]

