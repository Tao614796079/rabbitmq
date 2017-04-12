package com.biz._07delayqueue;

import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2017/4/12.
 */
public class Recv_direct {
    private static final String EXCHANGE_NAME = "delay";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("x-expires", 30 * 1000);//队列过期时间
        props.put("x-message-ttl", 12 * 1000);//队列上消息过期时间
        props.put("x-dead-letter-exchange", "exchange-direct");//过期消息转向路由
        props.put("x-dead-letter-routing-key", "routing-delay");//过期消息转向路由相匹配routingkey
        //创建一个临时队列
        String queueName=channel.queueDeclare("tmp01",false,false,false,props).getQueue();
        //绑定临时队列和转发器header_exchange
        channel.queueBind(queueName, EXCHANGE_NAME, "info");
        System.out.println("Received...");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = SerializationUtils.deserialize(body);
                String routingKey = envelope.getRoutingKey();
                System.out.println(" [x] Received : routingKey=" + routingKey + "message=" + message);
            }
        };
        //关闭自动应答机制，默认开启；这时候需要手动进行应该
        channel.basicConsume(queueName, false, consumer);
    }
}
