package com.study.im.common.model.message;

import com.study.im.common.model.ClientInfo;
import lombok.Data;

/**
 * 单聊消息内容
 *
 * @author lx
 * @date 2023/06/05
 */
@Data
public class MessageContent extends ClientInfo {

    /**
     * 消息id，前端生成
     */
    private String messageId;

    private String fromId;

    private String toId;

    private String messageBody;

    private Long messageTime;

    private String extra;

    private Long messageKey;

    /**
     * 消息序列，递增，用于消息有序性
     */
    private long messageSequence;

}
