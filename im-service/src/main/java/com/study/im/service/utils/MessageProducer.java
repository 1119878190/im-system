package com.study.im.service.utils;

import com.alibaba.fastjson.JSONObject;
import com.study.im.codec.proto.MessagePack;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.command.Command;
import com.study.im.common.model.ClientInfo;
import com.study.im.common.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 消息发送者
 *
 * @author lx
 * @date 2023/05/10
 */
@Slf4j
@Component
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserSessionUtils userSessionUtils;


    private String queueName = Constants.RabbitConstants.MessageService2Im;


    /**
     * 发送消息
     *
     * @param userSession 用户会话
     * @param msg         消息
     * @return boolean
     */
    public boolean sendMessage(UserSession userSession, Object msg) {
        try {
            log.info("send message == " + msg);
            // mq发送消息 需要指定端对应的broker为路由
            rabbitTemplate.convertAndSend(queueName, String.valueOf(userSession.getBrokerId()), msg);
            return true;
        } catch (AmqpException e) {
            log.error("send error : " + e.getMessage());
            return false;
        }
    }

    /**
     * 发送包
     *
     * @param toId        接收方
     * @param command     命令
     * @param msg         消息
     * @param userSession 用户会话
     * @return boolean
     */
    public boolean sendPack(String toId, Command command, Object msg, UserSession userSession) {

        MessagePack<Object> messagePack = new MessagePack<>();
        messagePack.setCommand(command.getCommand());
        messagePack.setToId(toId);
        messagePack.setClientType(userSession.getClientType());
        messagePack.setAppId(userSession.getAppId());
        messagePack.setImei(userSession.getImei());

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(msg));
        messagePack.setData(jsonObject);

        String str = JSONObject.toJSONString(messagePack);
        return sendMessage(userSession, str);
    }



    /**
     * 发送给用户
     *
     * @param toId       接收方userId
     * @param clientType 客户端类型
     * @param imei       imei
     * @param command    命令
     * @param data       数据
     * @param appId      应用程序id
     */
    public void sendToUser(String toId, Integer clientType, String imei, Command command, Object data, Integer appId) {
        if (Objects.isNull(clientType) && StringUtils.isBlank(imei)) {
            // 如果 client 和 imei 都为空， 说明是管理员调用，那么就要给用户所有端发送消息
            sendToUserAllClient(toId, command, data, appId);
        } else {
            // 否则 只给除了本端以外的端发送消息
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setClientType(clientType);
            clientInfo.setImei(imei);
            clientInfo.setAppId(appId);
            sendToUserExceptClient(toId, command, data, clientInfo);
        }

    }



    /**
     * 发送给某个用户所有客户端
     *
     * @param toId    接收方userId
     * @param command 命令
     * @param data    数据
     * @param appId   应用程序id
     */
    public void sendToUserAllClient(String toId, Command command, Object data, Integer appId) {
        List<UserSession> userSession = userSessionUtils.getUserSession(appId, toId);
        for (UserSession session : userSession) {
            sendPack(toId, command, data, session);
        }
    }



    /**
     * 发送给某个用户的指定客户端
     *
     * @param toId       接收方userId
     * @param command    命令
     * @param data       数据
     * @param clientInfo client信息
     */
    public void sendToUserAppointClient(String toId, Command command, Object data, ClientInfo clientInfo) {
        UserSession userSession = userSessionUtils.getUserSession(clientInfo.getAppId(),
                toId, clientInfo.getClientType(), clientInfo.getImei());
        sendPack(toId, command, data, userSession);

    }


    /**
     * 发送给某个用户除了某一个端的其它客户端
     *
     * @param toId       接收方userId
     * @param command    命令
     * @param data       数据
     * @param clientInfo 客户端信息
     */
    public void sendToUserExceptClient(String toId, Command command, Object data, ClientInfo clientInfo) {
        List<UserSession> userSession = userSessionUtils.getUserSession(clientInfo.getAppId(), toId);
        for (UserSession session : userSession) {
            if (!isMatch(session, clientInfo)) {
                sendPack(toId, command, data, session);
            }
        }
    }

    private boolean isMatch(UserSession sessionDto, ClientInfo clientInfo) {
        return Objects.equals(sessionDto.getAppId(), clientInfo.getAppId())
                && Objects.equals(sessionDto.getImei(), clientInfo.getImei())
                && Objects.equals(sessionDto.getClientType(), clientInfo.getClientType());
    }

}
