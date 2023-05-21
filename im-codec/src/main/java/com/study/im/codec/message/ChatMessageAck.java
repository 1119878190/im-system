package com.study.im.codec.message;

import lombok.Data;

/**
 * 单聊消息体
 *
 * @author lx
 * @date 2023/05/21
 */
@Data
public class ChatMessageAck {

    private String messageId;

    public ChatMessageAck(String messageId) {
        this.messageId = messageId;
    }
}
