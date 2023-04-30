package com.study.im.service.friendship.service;


import com.study.im.common.ResponseVO;
import com.study.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.study.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.study.im.service.friendship.model.req.DeleteFriendShipGroupReq;

/**
 * @author: lx
 * @description: 好友分组
 **/
public interface ImFriendShipGroupService {

    /**
     * 添加好友分组
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO addGroup(AddFriendShipGroupReq req);

    /**
     * 删除组
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO deleteGroup(DeleteFriendShipGroupReq req);

    /**
     * 获取好友分组信息
     *
     * @param fromId    从id
     * @param groupName 组名称
     * @param appId     应用程序id
     * @return {@link ResponseVO}<{@link ImFriendShipGroupEntity}>
     */
    ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId);


}
