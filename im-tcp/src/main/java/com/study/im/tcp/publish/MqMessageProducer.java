package com.study.im.tcp.publish;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.study.im.tcp.utils.MqFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * mq消息生成方
 *
 * @author lx
 * @date 2023/05/02
 */
@Slf4j
public class MqMessageProducer {


    public static void sendMessage(Object message) {
        Channel channel = null;
        // TODO: 2023/5/2 channelName 后续补充
        String channelName = "";
        try {
            channel = MqFactory.getChannel(channelName);
            channel.basicPublish(channelName, "", null, JSONObject.toJSONString(message).getBytes());

        } catch (Exception e) {
            log.error("发送消息异常:{}", e.getMessage());
        }


    }

}
