package com.study.im.codec.pack.message;

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

    private Long messageSequence;

    public ChatMessageAck(String messageId) {
        this.messageId = messageId;
    }

    public ChatMessageAck(String messageId, Long messageSequence) {
        this.messageId = messageId;
        this.messageSequence = messageSequence;
    }
}
