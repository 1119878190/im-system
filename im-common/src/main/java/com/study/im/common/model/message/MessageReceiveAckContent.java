package com.study.im.common.model.message;


import com.study.im.common.model.ClientInfo;
import lombok.Data;

/**
 * @description: 接收方消息确认
 * @author: lx
 * @version: 1.0
 */
@Data
public class MessageReceiveAckContent extends ClientInfo {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;


}
