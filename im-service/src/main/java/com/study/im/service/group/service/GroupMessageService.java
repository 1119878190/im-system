package com.study.im.service.group.service;

import cn.hutool.core.collection.CollectionUtil;
import com.study.im.codec.pack.message.ChatMessageAck;
import com.study.im.codec.pack.message.MessageReadPack;
import com.study.im.common.ResponseVO;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.command.GroupEventCommand;
import com.study.im.common.model.ClientInfo;
import com.study.im.common.model.message.GroupChatMessageContent;
import com.study.im.common.model.message.MessageReadContent;
import com.study.im.common.model.message.OfflineMessageContent;
import com.study.im.service.conversation.service.ConversationService;
import com.study.im.service.group.model.req.SendGroupMessageReq;
import com.study.im.service.message.model.resp.SendMessageResp;
import com.study.im.service.message.service.CheckSendMessageService;
import com.study.im.service.message.service.MessageStoreService;
import com.study.im.service.seq.RedisSeq;
import com.study.im.service.utils.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Autowired
    private MessageStoreService messageStoreService;

    @Autowired
    private RedisSeq redisSeq;

    @Resource
    private ConversationService conversationService;


    private final ThreadPoolExecutor threadPoolExecutor;

    {
        AtomicInteger num = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("message-group-thread-" + num.getAndIncrement());
                return thread;
            }
        });
    }


    /**
     * 群聊消息处理
     *
     * @param messageContent 要发送给client的消息
     */
    public void process(GroupChatMessageContent messageContent) {


        String fromId = messageContent.getFromId();
        String groupId = messageContent.getGroupId();
        Integer appId = messageContent.getAppId();
        // 前置校验
//        ResponseVO responseVO = imServerPermissionCheck(fromId, groupId, appId);
        // 消息幂等性： 缓存中是否存在，存在就只发送不进行二次持久化
        GroupChatMessageContent groupMessageCache = messageStoreService.getMessageFromMessageIdCache(appId, messageContent.getMessageId(), GroupChatMessageContent.class);
        if (Objects.nonNull(groupMessageCache)) {
            threadPoolExecutor.execute(() -> {
                // 1.回ack给自己，表示消息已发送成功
                ack(groupMessageCache, ResponseVO.successResponse());
                // 2.发送消息同步到本方其它线端
                syncToSender(groupMessageCache, groupMessageCache);
                // 3.发送消息给对方在线端
                dispatchMessage(groupMessageCache);
            });
        }

        // 消息有序性：递增标识seq
        long seq = redisSeq.doGetSeq(messageContent.getAppId() + ":" + Constants.SeqConstants.GroupMessage + ":" + messageContent.getGroupId());
        messageContent.setMessageSequence(seq);

        threadPoolExecutor.execute(() -> {
            // 群组消息持久化--读扩散
            messageStoreService.storeGroupMessage(messageContent);

            // 存储离线消息到redis
            List<String> groupMemberId = imGroupMemberService.getGroupMemberId(messageContent.getGroupId(), messageContent.getAppId());
            messageContent.setMemberId(groupMemberId);
            OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
            BeanUtils.copyProperties(messageContent, offlineMessageContent);
            offlineMessageContent.setToId(messageContent.getGroupId());
            messageStoreService.storeGroupOfflineMessage(offlineMessageContent, groupMemberId);

            // 1.回ack给自己，表示消息已发送成功
            ack(messageContent, ResponseVO.successResponse());
            // 2.发送消息同步到本方其它线端
            syncToSender(messageContent, messageContent);
            // 3.发送消息给对方在线端
            dispatchMessage(messageContent);

            // 存入缓存
            messageStoreService.setMessageFromMessageIdCache(appId, messageContent.getMessageId(), messageContent);

        });

    }


    /**
     * 分发消息
     *
     * @param messageContent 消息内容
     */
    private void dispatchMessage(GroupChatMessageContent messageContent) {
        List<String> groupMemberId;
        if (CollectionUtil.isEmpty(messageContent.getMemberId())) {
            groupMemberId = imGroupMemberService.getGroupMemberId(messageContent.getGroupId(), messageContent.getAppId());
        } else {
            groupMemberId = messageContent.getMemberId();
        }
        for (String memberId : groupMemberId) {
            if (!memberId.equals(messageContent.getFromId())) {
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
    public ResponseVO imServerPermissionCheck(String fromId, String groupId, Integer appId) {

        // 校验是否可以发送群消息
        ResponseVO responseVO = checkSendMessageService.checkGroupMessage(fromId, groupId, appId);
        return responseVO;
    }


    public SendMessageResp send(SendGroupMessageReq req) {
        SendMessageResp sendMessageResp = new SendMessageResp();
        GroupChatMessageContent message = new GroupChatMessageContent();
        BeanUtils.copyProperties(req, message);

        // 持久化消息
        messageStoreService.storeGroupMessage(message);

        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());
        //2.发消息给同步在线端
        syncToSender(message, message);
        //3.发消息给对方在线端
        dispatchMessage(message);

        return sendMessageResp;

    }

    /**
     * 消息已读
     *
     * @param messageReadContent 消息读取内容
     */
    public void readMark(MessageReadContent messageReadContent) {
        // 1.更新会话已读消息的seq递增标识
        conversationService.messageMarkRead(messageReadContent);

        // 2.通知消息接收方在线的端，接收方已读消息
        MessageReadPack messageReadPack = new MessageReadPack();
        BeanUtils.copyProperties(messageReadContent, messageReadPack);
        syncToSender(messageReadContent, messageReadPack);

        if (!messageReadContent.getFromId().equals(messageReadContent.getToId())) {
            // 3.发送给消息发送方，告知接收方已读消息
            messageProducer.sendToUserAllClient(messageReadContent.getToId(), GroupEventCommand.MSG_GROUP_READED_RECEIPT,
                    messageReadPack, messageReadContent.getAppId());
        }


    }

    /**
     * 同步消息已读到接收方其它端
     *
     * @param messageReadContent 消息读取内容
     * @param messageReadPack    消息读取包
     */
    private void syncToSender(MessageReadContent messageReadContent, MessageReadPack messageReadPack) {
        // 发送给自己的其它端，某一端已读消息
        // 消息已读command是由消息接收方发送，故这里是fromId，同步到from的其它端
        messageProducer.sendToUserExceptClient(messageReadContent.getFromId(), GroupEventCommand.MSG_GROUP_READED_NOTIFY,
                messageReadPack, messageReadContent);


    }
}
