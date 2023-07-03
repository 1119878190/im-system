package com.study.im.tcp.receiver;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.study.im.codec.proto.MessagePack;
import com.study.im.common.constant.Constants;
import com.study.im.tcp.receiver.process.BaseProcess;
import com.study.im.tcp.receiver.process.ProcessFactory;
import com.study.im.tcp.utils.MqFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

/**
 * mq消息接收器
 *
 * @author lx
 * @date 2023/05/02
 */
@Slf4j
public class MqMessageReceiver {

    private static Integer brokerId;

    /**
     * 开始接收消息  用户接收业务服务发送到mq的数据
     */
    private static void startReceiveMessage() {
        try {
            Channel channel = MqFactory.getChannel(Constants.RabbitConstants.MessageService2Im + brokerId);
            // Declare an exchange
            channel.exchangeDeclare(Constants.RabbitConstants.MessageService2Im, "direct");
            // Declare a queue
            channel.queueDeclare(Constants.RabbitConstants.MessageService2Im + brokerId,
                    true, false, false, null);
            //  Bind a queue to an exchange ，路由为 brokerId
            channel.queueBind(Constants.RabbitConstants.MessageService2Im + brokerId,
                    Constants.RabbitConstants.MessageService2Im, String.valueOf(brokerId));
            // queue consumer
            channel.basicConsume(Constants.RabbitConstants.MessageService2Im + brokerId, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    try {
                        String msgStr = new String(body);
                        log.info("服务端监听消息信息为 {} ", msgStr);

                        // 消息写入数据通道
                        MessagePack messagePack = JSONObject.parseObject(msgStr, MessagePack.class);
                        BaseProcess messageProcess = ProcessFactory.getMessageProcess(messagePack.getCommand());
                        messageProcess.process(messagePack);

                        // 消息成功写入通道后发送应答 Ack
                        channel.basicAck(envelope.getDeliveryTag(), false);

                    } catch (Exception e) {
                        e.printStackTrace();

                        // 消息不能正常写入通道，发送失败应答 NAck
                        channel.basicNack(envelope.getDeliveryTag(), false, false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void init(){
        startReceiveMessage();
    }


    public static void init(Integer brokerId){
        if (Objects.isNull(MqMessageReceiver.brokerId)){
            MqMessageReceiver.brokerId = brokerId;
        }
        startReceiveMessage();
    }
}
