FROM maven:3-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml /app/../pom.xml
COPY passenger-service/pom.xml ./pom.xml
COPY passenger-service/src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=build /app/target/*.jar passenger-service.jar
ENTRYPOINT [ "java", "-jar", "passenger-service.jar" ]

