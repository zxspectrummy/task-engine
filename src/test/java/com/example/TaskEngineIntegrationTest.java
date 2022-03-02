package com.example;

import com.example.domain.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.net.URI;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@Testcontainers
public class TaskEngineIntegrationTest {

    @Container
    public static DockerComposeContainer environment =
        new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
            .withLocalCompose(true)
            .withExposedService("rabbitmq", 5672)
            .withExposedService("postgres", 5432);


    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void testOkResponse() throws Exception {
        runPublisher();
        runSubscriber();
        Task task = new Task(UUID.randomUUID(), "ping -n3 8.8.8.8");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setExpires(0);
        requestHeaders.setCacheControl("private, no-store, max-age=0");
        HttpEntity<Task> request = new HttpEntity<>(task, requestHeaders);
        ResponseEntity<Task> result = restTemplate.postForEntity(new URI("http://localhost:8080/api/v1/tasks"), request, Task.class);
        Assertions.assertEquals(201, result.getStatusCodeValue());
    }

    private void runPublisher() {
        new SpringApplicationBuilder(Application.class)
            .profiles("pub")
            .run();
    }

    private void runSubscriber() {
        new SpringApplicationBuilder(Application.class)
            .profiles("sub")
            .properties("spring.main.web-application-type=none")
            .run();
    }
}