package com.study.im.service.friendship.service;


import com.study.im.common.ResponseVO;
import com.study.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.study.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;

/**
 * @author: lx
 * @description: 好友分组成员
 **/
public interface ImFriendShipGroupMemberService {

    /**
     * 添加小组成员
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO addGroupMember(AddFriendShipGroupMemberReq req);

    /**
     * 删除小组成员
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO delGroupMember(DeleteFriendShipGroupMemberReq req);

    /**
     * 添加小组成员 公共方法
     *
     * @param groupId 组id
     * @param toId    为id
     * @return int
     */
    int doAddGroupMember(Long groupId, String toId);

    /**
     * 清楚小组成员
     *
     * @param groupId 组id
     * @return int
     */
    int clearGroupMember(Long groupId);
}
