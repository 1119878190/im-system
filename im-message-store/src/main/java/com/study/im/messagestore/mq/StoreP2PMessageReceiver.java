package com.study.im.messagestore.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.study.im.common.constant.Constants;
import com.study.im.messagestore.dao.ImMessageBodyEntity;
import com.study.im.messagestore.model.DoStoreP2PMessageDto;
import com.study.im.messagestore.service.StoreMessageService;
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
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 单聊消息接收并持久化
 *
 * @author lx
 * @date 2023/06/13
 */
@Service
public class StoreP2PMessageReceiver {

    private static Logger logger = LoggerFactory.getLogger(StoreP2PMessageReceiver.class);

    @Autowired
    private StoreMessageService storeMessageService;

    @RabbitListener(

            bindings = @QueueBinding(
                    // 绑定 MQ 队列
                    value = @Queue(value = Constants.RabbitConstants.StoreP2PMessage, declare = "true"),
                    // 绑定 MQ 交换机
                    exchange = @Exchange(value = Constants.RabbitConstants.StoreP2PMessage, declare = "true")
            ), concurrency = "1"

    )
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {

        String msg = new String(message.getBody(), "utf-8");
        logger.info("CHAT MSG FORM QUEUE: {}", msg);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            JSONObject jsonObject = JSON.parseObject(msg);

            DoStoreP2PMessageDto doStoreP2PMessageDto = jsonObject.toJavaObject(DoStoreP2PMessageDto.class);
            ImMessageBodyEntity messageBody = jsonObject.getObject("messageBody", ImMessageBodyEntity.class);
            doStoreP2PMessageDto.setMessageBodyEntity(messageBody);

            // 单聊消息持久化--写扩散
            storeMessageService.doStoreP2PMessage(doStoreP2PMessageDto);

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
