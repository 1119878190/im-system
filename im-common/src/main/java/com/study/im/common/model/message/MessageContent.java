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

    private String messageId;

    private String fromId;

    private String toId;

    private String messageBody;

    private Long messageTime;

    private String extra;

    private Long messageKey;

}
