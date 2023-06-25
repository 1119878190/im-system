package com.study.im.messagestore.service;

import com.study.im.common.model.message.GroupChatMessageContent;
import com.study.im.common.model.message.MessageContent;
import com.study.im.messagestore.dao.ImGroupMessageHistoryEntity;
import com.study.im.messagestore.dao.ImMessageBodyEntity;
import com.study.im.messagestore.dao.ImMessageHistoryEntity;
import com.study.im.messagestore.dao.mapper.ImGroupMessageHistoryMapper;
import com.study.im.messagestore.dao.mapper.ImMessageBodyMapper;
import com.study.im.messagestore.dao.mapper.ImMessageHistoryMapper;
import com.study.im.messagestore.model.DoStoreGroupMessageDto;
import com.study.im.messagestore.model.DoStoreP2PMessageDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 持久化消息
 *
 * @author lx
 * @date 2023/06/13
 */
@Service
public class StoreMessageService {


    /**
     * 消息内容
     */
    @Autowired
    private ImMessageBodyMapper imMessageBodyMapper;
    /**
     * 单聊消息索引记录
     */
    @Autowired
    private ImMessageHistoryMapper imMessageHistoryMapper;
    /**
     * 群聊消息索引记录
     */
    @Autowired
    private ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;

    /**
     * 单聊消息持久化--写扩散
     * <p>
     * 存一份消息内容和两个消息索引
     *
     * @param doStoreP2PMessageDto 参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void doStoreP2PMessage(DoStoreP2PMessageDto doStoreP2PMessageDto) {

        imMessageBodyMapper.insert(doStoreP2PMessageDto.getMessageBodyEntity());
        List<ImMessageHistoryEntity> list = extractToP2PMessageHistory(doStoreP2PMessageDto.getMessageContent(), doStoreP2PMessageDto.getMessageBodyEntity());
        imMessageHistoryMapper.insertBatchSomeColumn(list);

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
        fromHistory.setSequence(messageContent.getMessageSequence());

        ImMessageHistoryEntity toHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, toHistory);
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setMessageKey(imMessageBodyEntity.getMessageKey());
        toHistory.setCreateTime(System.currentTimeMillis());
        toHistory.setSequence(messageContent.getMessageSequence());

        list.add(fromHistory);
        list.add(toHistory);
        return list;
    }

    /**
     * 群聊消息持久化
     *
     * @param doStoreGroupMessageDto 消息
     */
    @Transactional(rollbackFor = Exception.class)
    public void doStoreGroupMessage(DoStoreGroupMessageDto doStoreGroupMessageDto) {
        imMessageBodyMapper.insert(doStoreGroupMessageDto.getMessageBodyEntity());
        ImGroupMessageHistoryEntity imGroupMessageHistoryEntity = extractToGroupMessageHistory(doStoreGroupMessageDto.getGroupChatMessageContent(), doStoreGroupMessageDto.getMessageBodyEntity());
        imGroupMessageHistoryMapper.insert(imGroupMessageHistoryEntity);
    }

    private ImGroupMessageHistoryEntity extractToGroupMessageHistory(GroupChatMessageContent groupChatMessageContent,
                                                                     ImMessageBodyEntity messageBodyEntity) {
        ImGroupMessageHistoryEntity result = new ImGroupMessageHistoryEntity();
        BeanUtils.copyProperties(groupChatMessageContent, result);
        result.setGroupId(groupChatMessageContent.getGroupId());
        result.setMessageKey(messageBodyEntity.getMessageKey());
        result.setCreateTime(System.currentTimeMillis());
        return result;
    }


}
