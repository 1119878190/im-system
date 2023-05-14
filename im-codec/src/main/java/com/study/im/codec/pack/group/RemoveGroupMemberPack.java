package com.study.im.codec.pack.group;

import lombok.Data;

/**
 * @author: lx
 * @description: 踢人出群通知报文
 **/
@Data
public class RemoveGroupMemberPack {

    private String groupId;

    private String member;

}
