package com.study.im.service.message.service;

import com.alibaba.fastjson.JSONObject;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.DelFlagEnum;
import com.study.im.common.model.message.DoStoreP2PMessageDto;
import com.study.im.common.model.message.GroupChatMessageContent;
import com.study.im.common.model.message.ImMessageBody;
import com.study.im.common.model.message.MessageContent;
import com.study.im.service.group.dao.ImGroupMessageHistoryEntity;
import com.study.im.service.message.dao.ImMessageBodyEntity;
import com.study.im.service.message.dao.ImMessageHistoryEntity;
import com.study.im.service.utils.SnowflakeIdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
     *
     * @param groupChatMessageContent 群组聊天消息内容
     */
    @Transactional(rollbackFor = Exception.class)
    public void storeGroupMessage(GroupChatMessageContent groupChatMessageContent) {

        // imMessageBodyEntity
//        ImMessageBodyEntity imMessageBodyEntity = extractMessageBody(groupChatMessageContent);
//        imMessageBodyMapper.insert(imMessageBodyEntity);
//
//        // ImGroupMessageHistoryEntity
//        ImGroupMessageHistoryEntity imGroupMessageHistoryEntity = extractToGroupMessageHistory(groupChatMessageContent, imMessageBodyEntity);
//        imGroupMessageHistoryMapper.insert(imGroupMessageHistoryEntity);
//
//        groupChatMessageContent.setMessageKey(imMessageBodyEntity.getMessageKey());
    }



    private ImGroupMessageHistoryEntity extractToGroupMessageHistory(GroupChatMessageContent groupChatMessageContent,
                                                                     ImMessageBodyEntity imMessageBodyEntity){
        ImGroupMessageHistoryEntity result = new ImGroupMessageHistoryEntity();
        BeanUtils.copyProperties(groupChatMessageContent,result);
        result.setGroupId(groupChatMessageContent.getGroupId());
        result.setMessageKey(imMessageBodyEntity.getMessageKey());
        result.setCreateTime(System.currentTimeMillis());
        return result;
    }

}
