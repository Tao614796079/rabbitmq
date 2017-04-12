package com.biz._07delayqueue;

import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/12.
 * 接收消息类
 * 使用HEADERS类型转发
 */
public class Recv_headers {
    public static final String EXCHANGE_NAME = "header_exchange";
    public static void main(String[] args) throws Exception {
        recvAToB();
    }

    /**
     * 实验时启动接收类创建队列后，关闭该线程，使其进入未使用状态
     * @throws Exception
     */
    public static void recvAToB() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-expires", 30 * 1000);//队列过期时间
        args.put("x-message-ttl", 12 * 1000);//队列上消息过期时间
        args.put("x-dead-letter-exchange", "exchange-direct");//过期消息转向路由
        args.put("x-dead-letter-routing-key", "routing-delay");//过期消息转向路由相匹配routingkey
        //创建一个临时队列
        String queueName=channel.queueDeclare("tmp01",false,false,false,args).getQueue();
        //指定headers的匹配类型(all、any)、键值对
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("x-match", "all");//all any(只要有一个键值对匹配即可)
        headers.put("key","123456");
        //绑定临时队列和转发器header_exchange
        channel.queueBind(queueName, EXCHANGE_NAME, "info");
        System.out.println("Received...");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = SerializationUtils.deserialize(body);
                System.out.println(envelope.getRoutingKey() + ":Received :'" + message + "' done");
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        //关闭自动应答机制，默认开启；这时候需要手动进行应该
        channel.basicConsume(queueName, false, consumer);
    }
}

