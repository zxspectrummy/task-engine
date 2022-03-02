package com.example.rpc.handlers;

import com.example.Utils;
import com.example.domain.CommandExecutionResult;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;

@Configuration
@Profile("sub")
public class Subscriber {

    @RabbitListener(queues = "${queue.request}")
    public CommandExecutionResult subscribeToRequestQueue(@Payload String requestMessage, Message message) {
        System.out.println("Received message :" + message.getBody());
        CommandExecutionResult result = Utils.shellExec(requestMessage);
        System.out.println("Completed processing and sending the message :" + result);
        return result;
    }
}
