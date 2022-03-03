FROM openjdk:17-jdk-alpine
COPY target/task-engine-0.0.1-SNAPSHOT.jar task-engine-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-Dspring.profiles.active=sub","-jar","/task-engine-0.0.1-SNAPSHOT.jar"]