package com.study.im.messagestore.model;

import com.study.im.common.model.message.MessageContent;
import com.study.im.messagestore.dao.ImMessageBodyEntity;
import lombok.Data;

/**
 * @description:
 * @author: lx
 * @version: 1.0
 */
@Data
public class DoStoreP2PMessageDto {

    private MessageContent messageContent;

    private ImMessageBodyEntity messageBodyEntity;

}
