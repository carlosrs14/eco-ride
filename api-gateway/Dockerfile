FROM maven:3-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml /app/../pom.xml
COPY api-gateway/pom.xml ./pom.xml
COPY api-gateway/src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=build /app/target/*.jar api-gateway.jar
ENTRYPOINT [ "java", "-jar", "/api-gateway.jar" ]

