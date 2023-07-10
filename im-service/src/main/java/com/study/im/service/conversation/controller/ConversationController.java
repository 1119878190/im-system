package com.study.im.service.conversation.controller;

import com.study.im.common.ResponseVO;
import com.study.im.common.model.SyncReq;
import com.study.im.service.conversation.model.DeleteConversationReq;
import com.study.im.service.conversation.model.UpdateConversationReq;
import com.study.im.service.conversation.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/conversation")
public class ConversationController {


    @Autowired
    ConversationService conversationService;

    /**
     * 删除对话
     *
     * @param req        要求事情
     * @return {@link ResponseVO}
     */
    @RequestMapping("/deleteConversation")
    public ResponseVO deleteConversation(@RequestBody @Validated DeleteConversationReq req)  {
        return conversationService.deleteConversation(req);
    }

    /**
     * 更新会话
     *
     * @param req        要求事情
     * @return {@link ResponseVO}
     */
    @RequestMapping("/updateConversation")
    public ResponseVO updateConversation(@RequestBody @Validated UpdateConversationReq req)  {
        return conversationService.updateConversation(req);
    }


    /**
     * 增量同步会话列表
     *
     * @param req   req
     * @return {@link ResponseVO}
     */
    @RequestMapping("/syncConversationList")
    public ResponseVO syncFriendShipList(@RequestBody @Validated SyncReq req)  {
        return conversationService.syncConversationSet(req);
    }
}
