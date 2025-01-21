FROM openjdk:17-alpine

VOLUME /tmp

EXPOSE 8080 8080

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]