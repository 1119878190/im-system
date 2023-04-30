package com.study.im.service.friendship.service;


import com.study.im.common.ResponseVO;
import com.study.im.service.friendship.model.req.ApproverFriendRequestReq;
import com.study.im.service.friendship.model.req.FriendDto;
import com.study.im.service.friendship.model.req.ReadFriendShipRequestReq;

public interface ImFriendShipRequestService {

    /**
     * 添加好友亲求
     *
     * @param fromId 从id
     * @param dto    dto
     * @param appId  应用程序id
     * @return {@link ResponseVO}
     */
    ResponseVO addFriendshipRequest(String fromId, FriendDto dto, Integer appId);

    /**
     * 同意添加好友请求
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO approverFriendRequest(ApproverFriendRequestReq req);

    /**
     * 已读添加好友亲求
     * 把所有的请求都标记为已读
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO readFriendShipRequestReq(ReadFriendShipRequestReq req);

    /**
     * 查询添加好友请求
     *
     * @param fromId 从id
     * @param appId  应用程序id
     * @return {@link ResponseVO}
     */
    ResponseVO getFriendRequest(String fromId, Integer appId);
}
