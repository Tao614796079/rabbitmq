package com.biz._05topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2017/4/11.
 */
public class ReceiveLogsTopicForKenel {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //申明装器
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        //随机生成一个队列
        String queueName = channel.queueDeclare().getQueue();
        //接收所有与kernel有关的消息
        channel.queueBind(queueName, EXCHANGE_NAME, "kernel.*");

        System.out.println(" [*] Waiting for messages about kernel. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                String routingKey = envelope.getRoutingKey();
                System.out.println(" [x] Received routingKey = " + routingKey
                        + ",msg = " + message + ".");
            }
        };
        channel.basicConsume(queueName, consumer);
    }
}