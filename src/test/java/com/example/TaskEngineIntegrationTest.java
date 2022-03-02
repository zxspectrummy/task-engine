package com.example;

import com.example.domain.Task;
import com.example.domain.TaskState;
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
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@Testcontainers
public class TaskEngineIntegrationTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private final String baseUrl = "http://localhost:8080/api/v1/tasks";

    @Container
    public static DockerComposeContainer environment =
        new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
            .withLocalCompose(true)
            .withExposedService("rabbitmq", 5672)
            .withExposedService("postgres", 5432);


    @Test
    void testOkResponse() throws Exception {
        runPublisher();
        runSubscriber();
        Task task = new Task().withCommand("ping -n 5 8.8.8.8");
        System.out.println(baseUrl);
        HttpEntity<Task> request = new HttpEntity<>(task, new HttpHeaders());
        ResponseEntity<Task> result = restTemplate.postForEntity(new URI(baseUrl), request, Task.class);
        Assertions.assertEquals(201, result.getStatusCodeValue());
        UUID id = result.getBody().getId();
        await().atMost(15, SECONDS).until(taskIsFinished(id));
    }

    private Callable<Boolean> taskIsFinished(UUID id) {
        return () ->  {
            ResponseEntity<Task> response = restTemplate.getForEntity(new URI(String.format("%s/%s",baseUrl,id)), Task.class);
            return response.getBody().getState() == TaskState.FINISHED;
        };
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