package com.study.im.service.message.service;

import com.study.im.codec.message.ChatMessageAck;
import com.study.im.codec.message.MessageReceiveServerAckPack;
import com.study.im.common.ResponseVO;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.command.MessageCommand;
import com.study.im.common.model.ClientInfo;
import com.study.im.common.model.message.MessageContent;
import com.study.im.common.model.message.MessageReceiveAckContent;
import com.study.im.service.message.model.req.SendMessageReq;
import com.study.im.service.message.model.resp.SendMessageResp;
import com.study.im.service.seq.RedisSeq;
import com.study.im.service.utils.ConversationIdGenerate;
import com.study.im.service.utils.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Autowired
    private MessageStoreService messageStoreService;

    @Autowired
    private RedisSeq redisSeq;


    private final ThreadPoolExecutor threadPoolExecutor;


    {
        AtomicInteger atomicInteger = new AtomicInteger();
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("message-process-thread-" + atomicInteger.getAndIncrement());
                return thread;
            }
        });


    }


    /**
     * 单聊消息处理
     *
     * @param messageContent 要发送给client的消息
     */
    public void process(MessageContent messageContent) {


        String fromId = messageContent.getFromId();
        String toId = messageContent.getToId();
        Integer appId = messageContent.getAppId();

        // 消息幂等：从缓存中获取消息，如果存在，说明已经持久化了，那么短时间内不进行二次消息持久化，只发送
        MessageContent messageCache = messageStoreService.getMessageFromMessageIdCache(appId, messageContent.getMessageId(), MessageContent.class);
        if (Objects.nonNull(messageCache)) {
            threadPoolExecutor.execute(() -> {
                // 1.回ack给自己，表示消息已发送成功至服务器
                ack(messageCache, ResponseVO.successResponse());
                // 2.发送消息同步到其它线端
                syncToSender(messageCache, messageCache);
                // 3.发送消息给对方在线端
                List<ClientInfo> clientInfos = dispatchMessage(messageCache);
                if (clientInfos.isEmpty()) {
                    // 对方不在线，服务端自己发送消息确认给发送者
                    notOnlineReceiveAck(messageContent);
                }
            });
            return;
        }

        // 前置校验(这一块提到了im层)


        // 消息有序性：seq 递增标识,用户消息的有序性
        // 生成规则： appId ： Seq ： (from + to) groupId，其中 from + to 哪个大哪个就在前面
        long seq = redisSeq.doGetSeq(appId + ":" + Constants.SeqConstants.Message + ":" +
                ConversationIdGenerate.generateP2PId(fromId, toId));
        messageContent.setMessageSequence(seq);

        threadPoolExecutor.execute(() -> {

            // 消息持久化
            messageStoreService.storeP2PMessage(messageContent);

            // 1.回ack给自己，表示消息已发送成功至服务器
            ack(messageContent, ResponseVO.successResponse());
            // 2.发送消息同步到其它线端
            syncToSender(messageContent, messageContent);
            // 3.发送消息给对方在线端
            List<ClientInfo> clientInfos = dispatchMessage(messageContent);

            // 将消息缓存
            messageStoreService.setMessageFromMessageIdCache(appId, messageContent.getMessageId(), messageContent);

            if (clientInfos.isEmpty()) {
                // 对方不在线，服务端自己发送消息确认给发送者
                notOnlineReceiveAck(messageContent);
            }
        });

    }


    /**
     * 分发消息
     *
     * @param messageContent 消息内容
     * @return
     */
    private List<ClientInfo> dispatchMessage(MessageContent messageContent) {
        return messageProducer.sendToUserAllClient(messageContent.getToId(), MessageCommand.MSG_P2P, messageContent, messageContent.getAppId());
    }

    /**
     * IM 服务ack 确认， 表示消息已发送成功至服务器
     *
     * @param messageContent 消息内容
     * @param responseVO     回答签证官
     */
    private void ack(MessageContent messageContent, ResponseVO responseVO) {
        logger.info("msg ack,msgId={},checkResult={}", messageContent.getMessageId(), responseVO.getCode());

        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId(), messageContent.getMessageSequence());
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
     * @param fromId 发送者id
     * @param toId   接收者id
     * @param appId  appId
     * @return {@link ResponseVO}
     */
    public ResponseVO imServerPermissionCheck(String fromId, String toId, Integer appId) {

        ResponseVO responseVO = checkSendMessageService.checkSenderForbidAndMute(fromId, appId);
        if (!responseVO.isOk()) {
            return responseVO;
        }

        return responseVO = checkSendMessageService.checkFriendShip(fromId, toId, appId);
    }


    public SendMessageResp send(SendMessageReq req) {
        SendMessageResp sendMessageResp = new SendMessageResp();
        MessageContent message = new MessageContent();
        BeanUtils.copyProperties(req, message);
        // 持久化消息
        messageStoreService.storeP2PMessage(message);
        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());

        //2.发消息给同步在线端
        syncToSender(message, message);
        //3.发消息给对方在线端
        dispatchMessage(message);
        return sendMessageResp;
    }


    /**
     * 接收端收到消息确认
     *
     * @param messageReceiveAckContent 消息
     */
    public void receiveMark(MessageReceiveAckContent messageReceiveAckContent) {
        messageProducer.sendToUserAllClient(messageReceiveAckContent.getToId(), MessageCommand.MSG_RECEIVE_ACK,
                messageReceiveAckContent, messageReceiveAckContent.getAppId());
    }

    /**
     * 接收端不在线，服务器代发送消息确认ack
     *
     * @param messageContent 消息内容
     */
    public void notOnlineReceiveAck(MessageContent messageContent) {
        MessageReceiveServerAckPack pack = new MessageReceiveServerAckPack();
        pack.setFromId(messageContent.getToId());
        pack.setToId(messageContent.getFromId());
        pack.setMessageKey(messageContent.getMessageKey());
        pack.setMessageSequence(messageContent.getMessageSequence());
        // 标记为服务器发送的消息
        pack.setServerSend(true);
        messageProducer.sendToUserAppointClient(messageContent.getFromId(), MessageCommand.MSG_RECEIVE_ACK,
                pack, new ClientInfo(messageContent.getAppId(), messageContent.getClientType()
                        , messageContent.getImei()));

    }


}
