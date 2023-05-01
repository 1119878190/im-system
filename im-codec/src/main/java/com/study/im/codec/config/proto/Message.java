package com.study.im.codec.config.proto;

import lombok.Data;

/**
 * @description: 自定义编解码器的消息封装
 * @author: lx
 * @version: 1.0
 */
@Data
public class Message {

    private MessageHeader messageHeader;

    private Object messagePack;

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messagePack=" + messagePack +
                '}';
    }
}
