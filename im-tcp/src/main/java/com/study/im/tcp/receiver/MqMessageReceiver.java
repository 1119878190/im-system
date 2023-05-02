package com.study.im.tcp.receiver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.study.im.common.constant.Constants;
import com.study.im.tcp.utils.MqFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * mq消息接收器
 *
 * @author lx
 * @date 2023/05/02
 */
@Slf4j
public class MqMessageReceiver {

    private static void startReceiveMessage() {
        try {
            Channel channel = MqFactory.getChannel(Constants.RabbitConstants.MessageService2Im);
            channel.queueDeclare(Constants.RabbitConstants.MessageService2Im,
                    true, false, false, null);
            //  Bind a queue to an exchange
            channel.queueBind(Constants.RabbitConstants.MessageService2Im,
                    Constants.RabbitConstants.MessageService2Im,
                    "");
            // queue consumer
            channel.basicConsume(Constants.RabbitConstants.MessageService2Im, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    // TODO: 2023/5/2 处理消息服务发来的消息
                    String msgStr = new String(body);
                    log.info(msgStr);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void init(){
        startReceiveMessage();
    }

}
