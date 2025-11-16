FROM maven:3-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=build /app/target/*.jar car-service.jar
ENTRYPOINT [ "java", "-jar", "car-service.jar" ]

