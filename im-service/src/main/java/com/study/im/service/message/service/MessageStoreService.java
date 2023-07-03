package com.study.im.service.message.service;

import com.alibaba.fastjson.JSONObject;
import com.study.im.common.config.AppConfig;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.ConversationTypeEnum;
import com.study.im.common.enums.DelFlagEnum;
import com.study.im.common.model.message.*;
import com.study.im.service.conversation.service.ConversationService;
import com.study.im.service.group.dao.ImGroupMessageHistoryEntity;
import com.study.im.service.message.dao.ImMessageBodyEntity;
import com.study.im.service.message.dao.ImMessageHistoryEntity;
import com.study.im.service.utils.SnowflakeIdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 消息持久化
 * <p>
 * <p>
 * 思路：写扩散  单聊消息 写入两条history记录，但是消息体只存一份
 *
 * @author lx
 * @date 2023/06/07
 */
@Service
public class MessageStoreService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ConversationService conversationService;


    /**
     * 单聊消息持久化---写扩散
     * 由于我们单聊消息持久化采用的是 “写扩散”，故需要存两份消息历史ImMessageHistoryEntity，通过优化将消息体单独只存一份ImMessageBodyEntity
     *
     * @param messageContent 消息内容
     */
    public void storeP2PMessage(MessageContent messageContent) {
        // 通过mq异步消息持久化
        ImMessageBody imMessageBody = extractMessageBody(messageContent);

        messageContent.setMessageKey(imMessageBody.getMessageKey());
        DoStoreP2PMessageDto doStoreP2PMessageDto = new DoStoreP2PMessageDto();
        doStoreP2PMessageDto.setMessageBody(imMessageBody);
        doStoreP2PMessageDto.setMessageContent(messageContent);

        // 通过mq异步消息持久化
        rabbitTemplate.convertAndSend(Constants.RabbitConstants.StoreP2PMessage,
                "", JSONObject.toJSONString(doStoreP2PMessageDto));

    }

    /**
     * messageContent 转换 ImMessageBody
     *
     * @param messageContent 消息内容
     * @return {@link ImMessageBody}
     */
    public ImMessageBody extractMessageBody(MessageContent messageContent) {
        ImMessageBody messageBody = new ImMessageBody();
        messageBody.setAppId(messageContent.getAppId());
        messageBody.setMessageKey(SnowflakeIdWorker.nextId());
        messageBody.setCreateTime(System.currentTimeMillis());
        messageBody.setSecurityKey("");
        messageBody.setExtra(messageContent.getExtra());
        messageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());
        messageBody.setMessageTime(messageContent.getMessageTime());
        messageBody.setMessageBody(messageContent.getMessageBody());
        return messageBody;
    }


    /**
     * 封装为 消息历史 ImMessageHistoryEntity
     *
     * @param messageContent      消息内容
     * @param imMessageBodyEntity im消息体实体
     * @return {@link List}<{@link ImMessageHistoryEntity}>
     */
    public List<ImMessageHistoryEntity> extractToP2PMessageHistory(MessageContent messageContent,
                                                                   ImMessageBodyEntity imMessageBodyEntity) {
        List<ImMessageHistoryEntity> list = new ArrayList<>();

        ImMessageHistoryEntity fromHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, fromHistory);
        fromHistory.setOwnerId(messageContent.getFromId());
        fromHistory.setMessageKey(imMessageBodyEntity.getMessageKey());
        fromHistory.setCreateTime(System.currentTimeMillis());

        ImMessageHistoryEntity toHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, toHistory);
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setMessageKey(imMessageBodyEntity.getMessageKey());
        toHistory.setCreateTime(System.currentTimeMillis());

        list.add(fromHistory);
        list.add(toHistory);
        return list;
    }


    /**
     * 群组消息持久化---读扩散
     *
     * @param groupChatMessageContent 群组聊天消息内容
     */
    public void storeGroupMessage(GroupChatMessageContent groupChatMessageContent) {

        ImMessageBody imMessageBody = extractMessageBody(groupChatMessageContent);
        DoStoreGroupMessageDto dto = new DoStoreGroupMessageDto();
        dto.setGroupChatMessageContent(groupChatMessageContent);
        dto.setMessageBody(imMessageBody);

        // 通过mq异步消息持久化
        rabbitTemplate.convertAndSend(Constants.RabbitConstants.StoreGroupMessage,
                "", JSONObject.toJSONString(dto));
        groupChatMessageContent.setMessageKey(imMessageBody.getMessageKey());
    }


    private ImGroupMessageHistoryEntity extractToGroupMessageHistory(GroupChatMessageContent groupChatMessageContent,
                                                                     ImMessageBodyEntity imMessageBodyEntity) {
        ImGroupMessageHistoryEntity result = new ImGroupMessageHistoryEntity();
        BeanUtils.copyProperties(groupChatMessageContent, result);
        result.setGroupId(groupChatMessageContent.getGroupId());
        result.setMessageKey(imMessageBodyEntity.getMessageKey());
        result.setCreateTime(System.currentTimeMillis());
        return result;
    }


    /**
     * 将消息缓存
     *
     * @param messageContent 消息内容
     * @param appId          应用程序id
     * @param messageId      消息id
     */
    public void setMessageFromMessageIdCache(Integer appId, String messageId, Object messageContent) {
        // appId : cache : messageId
        String key = appId + ":" + Constants.RedisConstants.cacheMessage + ":" + messageId;
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(messageContent), 300, TimeUnit.SECONDS);
    }


    /**
     * 从缓存中查询消息
     *
     * @param appId     应用程序id
     * @param messageId 消息id
     * @param clazz     clazz
     * @return {@link T}
     */
    public <T> T getMessageFromMessageIdCache(Integer appId, String messageId,Class<T> clazz) {

        // appId : cache : messageId
        String key = appId + ":" + Constants.RedisConstants.cacheMessage + ":" + messageId;
        String msg = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(msg)) {
            return null;
        }
        return JSONObject.parseObject(msg, clazz);
    }

    /**
     * 存储单人离线消息
     *
     * 这里采用限制存储离线消息的条数(可以改成多少天)
     *
     * @param offlineMessage 离线消息
     */
    public void storeOfflineMessage(OfflineMessageContent offlineMessage) {

        // 找到fromId的队列
        String fromKey = offlineMessage.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + offlineMessage.getFromId();
        // 找到toId的队列
        String toKey = offlineMessage.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + offlineMessage.getToId();

        ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
        //判断 队列中的数据是否超过设定值
        if(operations.zCard(fromKey) > appConfig.getOfflineMessageCount()){
            // 删除第一个元素(最早的一条数据)
            operations.removeRange(fromKey,0,0);
        }
        offlineMessage.setConversationId(conversationService.convertConversationId(
                ConversationTypeEnum.P2P.getCode(),offlineMessage.getFromId(),offlineMessage.getToId()
        ));
        // 插入 数据 根据messageKey 作为分值
        operations.add(fromKey,JSONObject.toJSONString(offlineMessage),
                offlineMessage.getMessageKey());

        //判断 队列中的数据是否超过设定值
        if(operations.zCard(toKey) > appConfig.getOfflineMessageCount()){
            operations.removeRange(toKey,0,0);
        }
        offlineMessage.setConversationId(conversationService.convertConversationId(
                ConversationTypeEnum.P2P.getCode(),offlineMessage.getToId(),offlineMessage.getFromId()
        ));
        // 插入 数据 根据messageKey 作为分值
        operations.add(toKey,JSONObject.toJSONString(offlineMessage),
                offlineMessage.getMessageKey());


    }



    /**
     * @description: 存储群聊离线消息
     * @param
     * @return void
     * @author lld
     */
    public void storeGroupOfflineMessage(OfflineMessageContent offlineMessage
            , List<String> memberIds) {

        ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
        //判断 队列中的数据是否超过设定值
        offlineMessage.setConversationType(ConversationTypeEnum.GROUP.getCode());

        for (String memberId : memberIds) {
            // 找到toId的队列
            String toKey = offlineMessage.getAppId() + ":" +
                    Constants.RedisConstants.OfflineMessage + ":" +
                    memberId;
            offlineMessage.setConversationId(conversationService.convertConversationId(
                    ConversationTypeEnum.GROUP.getCode(), memberId, offlineMessage.getToId()
            ));
            if (operations.zCard(toKey) > appConfig.getOfflineMessageCount()) {
                operations.removeRange(toKey, 0, 0);
            }
            // 插入 数据 根据messageKey 作为分值
            operations.add(toKey, JSONObject.toJSONString(offlineMessage),
                    offlineMessage.getMessageKey());
        }


    }



}
