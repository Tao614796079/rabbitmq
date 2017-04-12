package com.biz._07delayqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2017/4/11.
 */
public class Send_direct {
    private static final String EXCHANGE_NAME = "delay";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String message = UUID.randomUUID().toString();
        String routing_key = "info";
        channel.basicPublish(EXCHANGE_NAME, routing_key, null, SerializationUtils.serialize(message));
        System.out.println(" [x] Sent routingKey = " + routing_key + " ,message = " + message + ".");
        channel.close();
        connection.close();
    }
}