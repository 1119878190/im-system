package com.study.im.codec.pack.friendship;

import lombok.Data;

/**
 * @author: lx
 * @description: 删除好友分组通知报文
 **/
@Data
public class DeleteFriendGroupPack {
    public String fromId;

    private String groupName;

    /** 序列号*/
    private Long sequence;
}
