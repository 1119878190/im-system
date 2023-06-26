package com.study.im.tcp.feign;

import com.study.im.common.ResponseVO;
import com.study.im.common.model.message.CheckSendMessageReq;
import feign.Headers;
import feign.RequestLine;

/**
 * Im通过feign调用业务服务接口
 *
 * @author lx
 * @date 2023/06/12
 */
public interface FeignMessageService {

    /**
     * 单聊消息前置校验
     *
     * @param o o
     * @return {@link ResponseVO}
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /message/checkSend")
    ResponseVO checkSendMessage(CheckSendMessageReq o);


    /**
     * 群聊消息前置校验
     *
     * @param o o
     * @return {@link ResponseVO}
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /message/checkGroupSend")
    ResponseVO checkGroupSendMessage(CheckSendMessageReq o);

}
