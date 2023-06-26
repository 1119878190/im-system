package com.study.im.service.message.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.command.MessageCommand;
import com.study.im.common.model.message.MessageContent;
import com.study.im.common.model.message.MessageReadContent;
import com.study.im.common.model.message.MessageReceiveAckContent;
import com.study.im.service.message.service.P2PMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * 业务服务接收--单聊消息并处理
 *
 * @author lx
 * @date 2023/05/19
 */
@Component
public class ChatOperateReceiver {

    private static Logger logger = LoggerFactory.getLogger(ChatOperateReceiver.class);

    @Autowired
    private P2PMessageService p2PMessageService;

    @RabbitListener(

            bindings = @QueueBinding(
                    // 绑定 MQ 队列
                    value = @Queue(value = Constants.RabbitConstants.Im2MessageService, declare = "true"),
                    // 绑定 MQ 交换机
                    exchange = @Exchange(value = Constants.RabbitConstants.Im2MessageService, declare = "true")
            ), concurrency = "1"

    )
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {

        String msg = new String(message.getBody(), "utf-8");
        logger.info("CHAT MSG FORM QUEUE: {}", msg);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            JSONObject jsonObject = JSON.parseObject(msg);
            Integer command = jsonObject.getInteger("command");

            if (command.equals(MessageCommand.MSG_P2P.getCommand())) {
                // 处理单聊消息
                MessageContent messageContent = jsonObject.toJavaObject(MessageContent.class);
                p2PMessageService.process(messageContent);
            }else if (command.equals(MessageCommand.MSG_RECEIVE_ACK.getCommand())){
                // 消息接收确认，由接收发主动发出
                MessageReceiveAckContent messageContent = jsonObject.toJavaObject(MessageReceiveAckContent.class);
                p2PMessageService.receiveMark(messageContent);
            }else if (command.equals(MessageCommand.MSG_READED.getCommand())){
                // 接收方，消息已读
                MessageReadContent messageReadContent = jsonObject.toJavaObject(MessageReadContent.class);
                p2PMessageService.readMark(messageReadContent);

            }
            // 给 rabbitmq 发送消息确认ack，消费者确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            logger.error("处理消息出现异常：{}", e.getMessage());
            logger.error("RMQ_CHAT_TRAN_ERROR", e);
            logger.error("NACK_MSG:{}", msg);
            //第一个false 表示不批量拒绝，第二个false表示不重回队列
            channel.basicNack(deliveryTag, false, false);
        }


    }

}
