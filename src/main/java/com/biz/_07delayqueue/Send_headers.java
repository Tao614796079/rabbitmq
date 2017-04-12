package com.biz._07delayqueue;

import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2017/4/12.
 */
public class Send_headers {
    public static final String EXCHANGE_NAME = "header_exchange";
    public static void main(String[] args) throws IOException, TimeoutException {
        sendAToB("Hello World!");
    }

    /**
     * 发送消息的方法
     * @param object 消息主体
     * @throws IOException
     * @throws TimeoutException
     */
    private static void sendAToB(Serializable object) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);//声明headers转发器
        //定义headers存储的键值对
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("key", "123456");
        headers.put("token", "654321");
        AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();
        properties.headers(headers);//把键值对放在properties
        properties.deliveryMode(2);//持久化
        channel.basicPublish(EXCHANGE_NAME, "info", properties.build(), SerializationUtils.serialize(object));
        System.out.println("Send '" + object + "'");
        channel.close();
        connection.close();
    }
}
