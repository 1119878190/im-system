package com.study.im.service.conversation.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.study.im.codec.pack.conversation.DeleteConversationPack;
import com.study.im.codec.pack.conversation.UpdateConversationPack;
import com.study.im.common.ResponseVO;
import com.study.im.common.config.AppConfig;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.ConversationErrorCode;
import com.study.im.common.enums.ConversationTypeEnum;
import com.study.im.common.enums.command.ConversationEventCommand;
import com.study.im.common.model.ClientInfo;
import com.study.im.common.model.message.MessageReadContent;
import com.study.im.service.conversation.dao.ImConversationSetEntity;
import com.study.im.service.conversation.dao.mapper.ImConversationSetMapper;
import com.study.im.service.conversation.model.DeleteConversationReq;
import com.study.im.service.conversation.model.UpdateConversationReq;
import com.study.im.service.seq.RedisSeq;
import com.study.im.service.utils.MessageProducer;
import com.study.im.service.utils.WriteUserSeq;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class ConversationService {


    @Resource
    private ImConversationSetMapper imConversationSetMapper;
    @Resource
    private MessageProducer messageProducer;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private RedisSeq redisSeq;
    @Autowired
    private WriteUserSeq writeUserSeq;


    public String convertConversationId(Integer type, String fromId, String toId) {
        return type + "_" + fromId + "_" + toId;
    }


    /**
     * 更新会话已读seq
     *
     * @param messageReadContent 消息读取内容
     */
    public void messageMarkRead(MessageReadContent messageReadContent) {

        String toId = messageReadContent.getToId();
        if (messageReadContent.getConversationType() == ConversationTypeEnum.GROUP.getCode()) {
            toId = messageReadContent.getGroupId();
        }
        String conversationId = convertConversationId(messageReadContent.getConversationType(),
                messageReadContent.getFromId(), toId);
        QueryWrapper<ImConversationSetEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conversation_id", conversationId);
        queryWrapper.eq("app_id", messageReadContent.getAppId());
        ImConversationSetEntity imConversationSetEntity = imConversationSetMapper.selectOne(queryWrapper);
        if (Objects.isNull(imConversationSetEntity)) {
            imConversationSetEntity = new ImConversationSetEntity();
            long seq = redisSeq.doGetSeq(messageReadContent.getAppId() + ":" + Constants.SeqConstants.Conversation);
            imConversationSetEntity.setConversationId(conversationId);
            BeanUtils.copyProperties(messageReadContent, imConversationSetEntity);
            imConversationSetEntity.setReadedSequence(messageReadContent.getMessageSequence());
            imConversationSetEntity.setSequence(seq);
            imConversationSetEntity.setToId(toId);
            imConversationSetMapper.insert(imConversationSetEntity);
            writeUserSeq.writeUserSeq(messageReadContent.getAppId(),
                    messageReadContent.getFromId(),Constants.SeqConstants.Conversation,seq);
        } else {
            long seq = redisSeq.doGetSeq(messageReadContent.getAppId() + ":" + Constants.SeqConstants.Conversation);
            imConversationSetEntity.setSequence(seq);
            imConversationSetEntity.setReadedSequence(messageReadContent.getMessageSequence());
            imConversationSetMapper.readMark(imConversationSetEntity);
            writeUserSeq.writeUserSeq(messageReadContent.getAppId(),
                    messageReadContent.getFromId(),Constants.SeqConstants.Conversation,seq);
        }
    }

    /**
     * @description: 删除会话
     * @param
     * @return com.lld.im.common.ResponseVO
     * @author lld
     */
    public ResponseVO deleteConversation(DeleteConversationReq req){

        //置顶 有免打扰
        //        QueryWrapper<ImConversationSetEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("conversation_id",req.getConversationId());
//        queryWrapper.eq("app_id",req.getAppId());
//        ImConversationSetEntity imConversationSetEntity = imConversationSetMapper.selectOne(queryWrapper);
//        if(imConversationSetEntity != null){
//            imConversationSetEntity.setIsMute(0);
//            imConversationSetEntity.setIsTop(0);
//            imConversationSetMapper.update(imConversationSetEntity,queryWrapper);
//        }

        // 多端同步删除会话
        if(appConfig.getDeleteConversationSyncMode() == 1){
            DeleteConversationPack pack = new DeleteConversationPack();
            pack.setConversationId(req.getConversationId());
            messageProducer.sendToUserExceptClient(req.getFromId(),
                    ConversationEventCommand.CONVERSATION_DELETE,
                    pack,new ClientInfo(req.getAppId(),req.getClientType(),
                            req.getImei()));
        }
        return ResponseVO.successResponse();
    }


    /**
     * @description: 更新会话 置顶or免打扰
     * @param
     * @return com.lld.im.common.ResponseVO
     * @author lld
     */
    public ResponseVO updateConversation(UpdateConversationReq req){


        if(req.getIsTop() == null && req.getIsMute() == null){
            return ResponseVO.errorResponse(ConversationErrorCode.CONVERSATION_UPDATE_PARAM_ERROR);
        }
        QueryWrapper<ImConversationSetEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conversation_id",req.getConversationId());
        queryWrapper.eq("app_id",req.getAppId());
        ImConversationSetEntity imConversationSetEntity = imConversationSetMapper.selectOne(queryWrapper);
        if(imConversationSetEntity != null){

            long seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.Conversation);

            if(req.getIsMute() != null){
                imConversationSetEntity.setIsTop(req.getIsTop());
            }
            if(req.getIsMute() != null){
                imConversationSetEntity.setIsMute(req.getIsMute());
            }
            imConversationSetEntity.setSequence(seq);
            imConversationSetMapper.update(imConversationSetEntity,queryWrapper);

            writeUserSeq.writeUserSeq(req.getAppId(),
                    req.getFromId(),Constants.SeqConstants.Conversation,seq);

            UpdateConversationPack pack = new UpdateConversationPack();
            pack.setConversationId(req.getConversationId());
            pack.setIsMute(imConversationSetEntity.getIsMute());
            pack.setIsTop(imConversationSetEntity.getIsTop());
            pack.setSequence(seq);
            pack.setConversationType(imConversationSetEntity.getConversationType());
            messageProducer.sendToUserExceptClient(req.getFromId(),
                    ConversationEventCommand.CONVERSATION_UPDATE,
                    pack,new ClientInfo(req.getAppId(),req.getClientType(),
                            req.getImei()));
        }
        return ResponseVO.successResponse();
    }


}
