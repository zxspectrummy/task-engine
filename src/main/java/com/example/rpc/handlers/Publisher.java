package com.example.rpc.handlers;

import com.example.domain.Task;
import com.example.domain.TaskRepository;
import com.example.domain.CommandExecutionResult;
import com.example.domain.TaskState;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


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
        task.setState(TaskState.IN_PROGRESS);
        taskRepository.save(task);
        System.out.println("Task:" + task.getId() + " is:" + task.getState());

        AsyncRabbitTemplate.RabbitConverterFuture<CommandExecutionResult> responseMessageRabbitConverterFuture = asyncRabbitTemplate
            .convertSendAndReceive(exchange, requestRoutingKey, task.getCommand());

        responseMessageRabbitConverterFuture.addCallback(
            responseMessage -> {
                System.out.println("Response for request message:" + task.getCommand() + " is " + responseMessage);
                task.setStderr(responseMessage.stderr());
                task.setStdout(responseMessage.stdout());
                task.setExitCode(responseMessage.exitCode());
                task.setState(TaskState.FINISHED);
                System.out.println("Task:" + task.getId() + " is:" + task.getState());

                taskRepository.save(task);
            },
            failure ->
                System.out.println(failure.getMessage())
        );
    }
}

