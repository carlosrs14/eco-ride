FROM maven:3-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml /app/../pom.xml
COPY config-server/pom.xml ./pom.xml
COPY config-server/src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine
RUN apk update && apk add --no-cache curl
VOLUME /tmp
COPY --from=build /app/target/*.jar config-server.jar
ENTRYPOINT [ "java", "-jar", "/config-server.jar" ]
