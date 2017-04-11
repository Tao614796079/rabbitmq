package com.biz._05topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2017/4/11.
 */
public class EmitLogTopic {
    private static final String EXCHANGE_NAME = "topic_logs";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String[] routing_keys = new String[] { "kernel.info", "cron.warning", "auth.info", "kernel.critical" };
        for (String routing_key : routing_keys) {
            String message = UUID.randomUUID().toString();
            channel.basicPublish(EXCHANGE_NAME, routing_key, null, message.getBytes());
            System.out.println(" [x] Sent routingKey = "+routing_key+" ,message = " + message + ".");
        }
        channel.close();
        connection.close();
    }
}