package com.study.im.common.model.message;

import lombok.Data;

/**
 * 群组聊天消息内容
 *
 * @author lx
 * @date 2023/06/05
 */
@Data
public class GroupChatMessageContent extends MessageContent {


    private String groupId;


}
