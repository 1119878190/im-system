package com.study.im.common.model.message;

import com.study.im.common.model.ClientInfo;
import lombok.Data;

/**
 * 消息已读
 *
 * @author lx
 * @date 2023/06/25
 */
@Data
public class MessageReadContent extends ClientInfo {

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
