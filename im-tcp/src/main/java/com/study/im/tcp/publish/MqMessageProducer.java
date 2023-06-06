package com.study.im.tcp.publish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.study.im.codec.proto.Message;
import com.study.im.common.constant.Constants;
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


    public static void sendMessage(Message message,Integer command) {
        Channel channel = null;
        String channelName = Constants.RabbitConstants.Im2MessageService;

        if (command.toString().startsWith("2")){
            // 如果消息是 2 开头的 ，说明是群组消息
            channelName = Constants.RabbitConstants.Im2GroupService;
        }

        try {
            channel = MqFactory.getChannel(channelName);

            JSONObject o = (JSONObject) JSON.toJSON(message.getMessagePack());
            o.put("command",command);
            o.put("clientType",message.getMessageHeader().getClientType());
            o.put("imei",message.getMessageHeader().getImei());
            o.put("appId",message.getMessageHeader().getAppId());
            // 发送消息给逻辑层
            channel.basicPublish(channelName, "", null, o.toJSONString().getBytes());
        } catch (Exception e) {
            log.error("发送消息异常:{}", e.getMessage());
        }


    }

}
