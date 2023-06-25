package com.study.im.messagestore.model;

import com.study.im.common.model.message.GroupChatMessageContent;
import com.study.im.messagestore.dao.ImMessageBodyEntity;
import lombok.Data;

/**
 * @description:
 * @author: lx
 * @version: 1.0
 */
@Data
public class DoStoreGroupMessageDto {

    private GroupChatMessageContent groupChatMessageContent;

    private ImMessageBodyEntity messageBodyEntity;

}
