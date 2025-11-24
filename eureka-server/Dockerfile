FROM maven:3-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml /app/../pom.xml
COPY eureka-server/pom.xml ./pom.xml
COPY eureka-server/src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine
RUN apk update && apk add --no-cache curl
VOLUME /tmp
COPY --from=build /app/target/*.jar /eureka-server.jar
ENTRYPOINT [ "java", "-jar", "/eureka-server.jar" ]
