package com.study.im.service.message.service;

import com.study.im.codec.message.ChatMessageAck;
import com.study.im.common.ResponseVO;
import com.study.im.common.enums.command.MessageCommand;
import com.study.im.common.model.ClientInfo;
import com.study.im.service.message.model.MessageContent;
import com.study.im.service.utils.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 单聊消息处理
 *
 * @author lx
 * @date 2023/05/19
 */
@Service
public class P2PMessageService {

    private Logger logger = LoggerFactory.getLogger(P2PMessageService.class);

    @Autowired
    private CheckSendMessageService checkSendMessageService;

    @Autowired
    private MessageProducer messageProducer;

    /**
     * 单聊消息处理
     *
     * @param messageContent 要发送给client的消息
     */
    public void process(MessageContent messageContent) {


        String fromId = messageContent.getFromId();
        String toId = messageContent.getToId();
        Integer appId = messageContent.getAppId();
        // 前置校验
        // 这个用户是否被禁言 是否被禁用
        // 发送方和接收方是否是好友
        ResponseVO responseVO = imServerPermissionCheck(fromId, toId, messageContent);
        if (responseVO.isOk()) {
            // 成功
            // 1.回ack给自己，表示消息已发送成功
            ack(messageContent, responseVO);
            // 2.发送消息同步到其它线端
            syncToSender(messageContent, messageContent);
            // 3.发送消息给对象在线端
            dispatchMessage(messageContent);

        } else {
            // 告诉客户端自己发送的消息失败了
            ack(messageContent, responseVO);
        }


    }


    /**
     * 分发消息
     *
     * @param messageContent 消息内容
     */
    private void dispatchMessage(MessageContent messageContent) {
        messageProducer.sendToUserAllClient(messageContent.getToId(), MessageCommand.MSG_P2P, messageContent, messageContent.getAppId());
    }

    /**
     * ack 确认
     *
     * @param messageContent 消息内容
     * @param responseVO     回答签证官
     */
    private void ack(MessageContent messageContent, ResponseVO responseVO) {
        logger.info("msg ack,msgId={},checkResult={}", messageContent.getMessageId(), responseVO.getCode());

        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        // 发送 ack 确认消息给发送者端
        messageProducer.sendToUserAppointClient(messageContent.getFromId(), MessageCommand.MSG_ACK, responseVO, messageContent);
    }

    /**
     * 同步到发送方
     *
     * @param messageContent 消息内容
     * @param clientInfo     客户端信息
     */
    private void syncToSender(MessageContent messageContent, ClientInfo clientInfo) {
        messageProducer.sendToUserExceptClient(messageContent.getFromId(), MessageCommand.MSG_P2P, messageContent, messageContent);
    }


    /**
     * 前置校验
     *
     * @param fromId         从id
     * @param toId           为id
     * @param messageContent 消息内容
     * @return {@link ResponseVO}
     */
    private ResponseVO imServerPermissionCheck(String fromId, String toId, MessageContent messageContent) {

        ResponseVO responseVO = checkSendMessageService.checkSenderForbidAndMute(fromId, messageContent.getAppId());
        if (!responseVO.isOk()) {
            return responseVO;
        }

        return responseVO = checkSendMessageService.checkFriendShip(fromId, toId, messageContent.getAppId());
    }


}
