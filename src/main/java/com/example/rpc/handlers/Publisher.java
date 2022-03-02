package com.example.rpc.handlers;

import com.example.domain.CommandExecutionResult;
import com.example.domain.Task;
import com.example.domain.TaskRepository;
import com.example.domain.TaskState;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.Timestamp;


@Configuration
@Profile("pub")
public class Publisher {

    @Value("${routingKey.request}")
    private String requestRoutingKey;

    @Value("${exchange.direct}")
    private String exchange;

    @Autowired
    private AsyncRabbitTemplate asyncRabbitTemplate;

    @Autowired
    private TaskRepository taskRepository;

    public void publish(Task task) {
        System.out.println("Sending out message on exchange " + task.getCommand());
        taskRepository.save(task.withState(TaskState.IN_PROGRESS)
            .withLastUpdatedAt(new Timestamp(System.currentTimeMillis())));
        System.out.println("Task:" + task.getId() + " is in progress");

        AsyncRabbitTemplate.RabbitConverterFuture<CommandExecutionResult> responseMessageRabbitConverterFuture = asyncRabbitTemplate
            .convertSendAndReceive(exchange, requestRoutingKey, task.getCommand());

        responseMessageRabbitConverterFuture.addCallback(
            responseMessage -> {
                System.out.println("Response for request message:" + task.getCommand() + " is " + responseMessage);
                Task finishedTask = task.withState(TaskState.FINISHED)
                    .withStdout(responseMessage.stdout())
                    .withStderr(responseMessage.stderr())
                    .withExitCode(responseMessage.exitCode())
                    .withLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
                System.out.println("Task:" + task.getId() + " is finished");
                taskRepository.save(finishedTask);
            },
            failure ->
                System.out.println(failure.getMessage())
        );
    }
}

