package com.study.im.service.group.service;

import com.study.im.codec.message.ChatMessageAck;
import com.study.im.common.ResponseVO;
import com.study.im.common.enums.command.GroupEventCommand;
import com.study.im.common.model.ClientInfo;
import com.study.im.common.model.message.GroupChatMessageContent;
import com.study.im.service.message.service.CheckSendMessageService;
import com.study.im.service.utils.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 群组消息处理
 *
 * @author lx
 * @date 2023/06/05
 */
@Component
public class GroupMessageService {

    private Logger logger = LoggerFactory.getLogger(GroupMessageService.class);

    @Autowired
    private CheckSendMessageService checkSendMessageService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private ImGroupMemberService imGroupMemberService;

    /**
     * 单聊消息处理
     *
     * @param messageContent 要发送给client的消息
     */
    public void process(GroupChatMessageContent messageContent) {


        String fromId = messageContent.getFromId();
        String groupId = messageContent.getGroupId();
        Integer appId = messageContent.getAppId();
        // 前置校验
        ResponseVO responseVO = imServerPermissionCheck(fromId, groupId, appId);
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
    private void dispatchMessage(GroupChatMessageContent messageContent) {

        List<String> groupMemberId = imGroupMemberService.getGroupMemberId(messageContent.getGroupId(), messageContent.getAppId());
        for (String memberId : groupMemberId) {
            if (!memberId.equals(messageContent.getFromId())){
                messageProducer.sendToUserAllClient(memberId,
                        GroupEventCommand.MSG_GROUP, messageContent, messageContent.getAppId());
            }

        }

    }

    /**
     * ack 确认
     *
     * @param messageContent 消息内容
     * @param responseVO     回答签证官
     */
    private void ack(GroupChatMessageContent messageContent, ResponseVO responseVO) {
        logger.info("msg ack,msgId={},checkResult={}", messageContent.getMessageId(), responseVO.getCode());

        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        // 发送 ack 确认消息给发送者端
        messageProducer.sendToUserAppointClient(
                messageContent.getFromId(),
                GroupEventCommand.GROUP_MSG_ACK,
                responseVO, messageContent);
    }

    /**
     * 同步到发送方
     *
     * @param messageContent 消息内容
     * @param clientInfo     客户端信息
     */
    private void syncToSender(GroupChatMessageContent messageContent, ClientInfo clientInfo) {
        messageProducer.sendToUserExceptClient(messageContent.getFromId(), GroupEventCommand.MSG_GROUP, messageContent, messageContent);
    }


    /**
     * 前置校验
     *
     * @param fromId  发送方id
     * @param groupId 群组Id
     * @param appId   appId
     * @return {@link ResponseVO}
     */
    private ResponseVO imServerPermissionCheck(String fromId, String groupId, Integer appId) {

        // 校验是否可以发送群消息
        ResponseVO responseVO = checkSendMessageService.checkGroupMessage(fromId, groupId, appId);
        return responseVO;
    }

}