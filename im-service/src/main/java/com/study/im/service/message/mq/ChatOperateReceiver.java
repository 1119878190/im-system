package com.study.im.service.message.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.command.MessageCommand;
import com.study.im.service.message.model.MessageContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author lx
 * @date 2023/05/19
 */
@Component
public class ChatOperateReceiver {

    private static Logger logger = LoggerFactory.getLogger(ChatOperateReceiver.class);


    @RabbitListener(

            bindings = @QueueBinding(
                    value = @Queue(value = Constants.RabbitConstants.Im2MessageService, declare = "true"),
                    exchange = @Exchange(value = Constants.RabbitConstants.Im2MessageService, declare = "true")
            ), concurrency = "1"

    )
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel) throws UnsupportedEncodingException {

        String msg = new String(message.getBody(), "utf-8");
        logger.info("CHAT MSG FORM QUEUE: {}", msg);

        try {
            JSONObject jsonObject = JSON.parseObject(msg);
            Integer command = jsonObject.getInteger("command");

            if (command.equals(MessageCommand.MSG_P2P.getCommand())){
                // 处理单聊消息
                MessageContent messageContent = jsonObject.toJavaObject(MessageContent.class);


            }

        }catch (Exception e){

        }


    }

}
