FROM openjdk:17-jdk-alpine
COPY target/task-queue-0.0.1-SNAPSHOT.jar task-queue-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-Dspring.profiles.active=sub","-Dspring.main.web-application-type=none","-jar","/task-queue-0.0.1-SNAPSHOT.jar"]