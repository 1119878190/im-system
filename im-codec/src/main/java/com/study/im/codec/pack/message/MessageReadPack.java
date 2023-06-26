package com.study.im.codec.pack.message;

import lombok.Data;

/**
 * 消息已读包
 *
 * @author lx
 * @date 2023/06/25
 */
@Data
public class MessageReadPack {


    /**
     * 消息序列
     */
    private Long messageSequence;

    private String fromId;

    private String toId;

    private String groupId;

    /**
     * 会话类型
     */
    private Integer conversationType;

}
