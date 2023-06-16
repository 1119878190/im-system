package com.study.im.codec.message;

import lombok.Data;

/**
 * @description: 接收方不在线，由服务器发送接收消息ack
 * @author: lx
 * @version: 1.0
 */
@Data
public class MessageReceiveServerAckPack {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

    /**
     * 是否服务器发送
     */
    private Boolean serverSend;
}
