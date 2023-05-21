package com.study.im.service.message.model;

import com.study.im.common.model.ClientInfo;
import lombok.Data;

@Data
public class MessageContent extends ClientInfo {

    private String messageId;

    private String fromId;

    private String toId;

    private String messageBody;

}
