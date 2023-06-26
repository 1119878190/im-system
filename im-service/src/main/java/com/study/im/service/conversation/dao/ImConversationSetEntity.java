package com.study.im.service.conversation.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 会话
 *
 * @author: lx
 * @description:
 **/
@Data
@TableName("im_conversation_set")
public class ImConversationSetEntity {

    //会话id 0_fromId_toId
    private String conversationId;

    //会话类型
    private Integer conversationType;

    private String fromId;

    private String toId;

    /**
     * 是否消息免打扰
     */
    private int isMute;

    /**
     * 是否置顶
     */
    private int isTop;

    private Long sequence;

    /**
     * 已读的seq序列号
     */
    private Long readedSequence;

    private Integer appId;
}
