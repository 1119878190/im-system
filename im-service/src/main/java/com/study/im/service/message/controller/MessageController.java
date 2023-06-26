package com.study.im.service.message.controller;

import com.study.im.common.ResponseVO;
import com.study.im.common.model.message.CheckSendMessageReq;
import com.study.im.service.group.service.GroupMessageService;
import com.study.im.service.message.model.req.SendMessageReq;
import com.study.im.service.message.service.P2PMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 消息controller
 *
 * @author lx
 * @date 2023/06/12
 */
@RestController
@RequestMapping("/v1/message")
public class MessageController {


    @Autowired
    private P2PMessageService p2PMessageService;
    @Resource
    private GroupMessageService groupMessageService;


    /**
     * 单聊发送
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    @RequestMapping("/send")
    public ResponseVO send(@RequestBody @Validated SendMessageReq req) {
        return ResponseVO.successResponse(p2PMessageService.send(req));
    }


    /**
     * 单聊消息发送前校验
     *
     * @param req req
     * @return {@link ResponseVO}
     */
    @RequestMapping("/checkSend")
    public ResponseVO checkSend(@RequestBody @Validated CheckSendMessageReq req) {
        return p2PMessageService.imServerPermissionCheck(req.getFromId(), req.getToId(), req.getAppId());
    }


    /**
     * 群聊消息发送前校验
     *
     * @param req req
     * @return {@link ResponseVO}
     */
    @RequestMapping("/checkGroupSend")
    public ResponseVO checkGroupSend(@RequestBody @Validated CheckSendMessageReq req) {
        return groupMessageService.imServerPermissionCheck(req.getFromId(), req.getToId(), req.getAppId());
    }

}
