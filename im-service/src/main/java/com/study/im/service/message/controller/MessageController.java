package com.study.im.service.message.controller;

import com.study.im.common.ResponseVO;
import com.study.im.service.message.model.req.SendMessageReq;
import com.study.im.service.message.service.P2PMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    /**
     * 单聊发送
     *
     * @param req   要求事情
     * @return {@link ResponseVO}
     */
    @RequestMapping("/send")
    public ResponseVO send(@RequestBody @Validated SendMessageReq req)  {
        return ResponseVO.successResponse(p2PMessageService.send(req));
    }




}
