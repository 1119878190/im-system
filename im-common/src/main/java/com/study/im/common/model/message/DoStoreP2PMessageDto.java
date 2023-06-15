package com.study.im.common.model.message;

import lombok.Data;

/**
 * @description:
 * @author: lx
 * @version: 1.0
 */
@Data
public class DoStoreP2PMessageDto {

    private MessageContent messageContent;

    private ImMessageBody messageBody;

}
