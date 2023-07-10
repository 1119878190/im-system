package com.study.im.service.friendship.service;


import com.study.im.common.ResponseVO;
import com.study.im.common.model.RequestBase;
import com.study.im.common.model.SyncReq;
import com.study.im.service.friendship.model.req.*;

/**
 * @description: 好友关系
 * @author: lx
 * @version: 1.0
 */
public interface ImFriendService {


    /**
     * 导入好友关系
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO importFriendShip(ImportFriendShipReq req);

    /**
     * 添加朋友
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO addFriend(AddFriendReq req);

    /**
     * 更新好友
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO updateFriend(UpdateFriendReq req);

    /**
     * 删除朋友
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO deleteFriend(DeleteFriendReq req);

    /**
     * 删除所有好友
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO deleteAllFriend(DeleteFriendReq req);

    /**
     * 获取所有好友
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO getAllFriendShip(GetAllFriendShipReq req);

    /**
     * 获取关系
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO getRelation(GetRelationReq req);

    /**
     * 添加朋友
     *
     * @param requestBase 请求基础
     * @param fromId      从id
     * @param dto         dto
     * @param appId       应用程序id
     * @return {@link ResponseVO}
     */
    ResponseVO doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId);


    /**
     * 校验好友关系
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO checkFriendship(CheckFriendShipReq req);


    /**
     * 拉黑
     *
     * @param req 亲求
     * @return {@link ResponseVO}
     */
    ResponseVO addBlack(AddFriendShipBlackReq req);

    /**
     * 取消拉黑
     *
     * @param req 亲求
     * @return {@link ResponseVO}
     */
    ResponseVO deleteBlack(DeleteBlackReq req);


    /**
     * 是否拉黑
     *
     * @param req 要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO checkBlack(CheckFriendShipReq req);


    /**
     * 增量同步好友列表
     *
     * @param req   要求事情
     * @return {@link ResponseVO}
     */
    ResponseVO syncFriendshipList(SyncReq req);
}
