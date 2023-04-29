package com.study.im.service.friendship.controller;


import com.study.im.common.ResponseVO;
import com.study.im.service.friendship.model.req.ApproverFriendRequestReq;
import com.study.im.service.friendship.model.req.GetFriendShipRequestReq;
import com.study.im.service.friendship.model.req.ReadFriendShipRequestReq;
import com.study.im.service.friendship.service.ImFriendShipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 添加好友亲求
 *
 * @author lx
 * @date 2023/04/29
 */
@RestController
@RequestMapping("v1/friendshipRequest")
public class ImFriendShipRequestController {

    @Autowired
    ImFriendShipRequestService imFriendShipRequestService;

    /**
     * 通过好友请求
     *
     * @param req        要求事情
     * @param appId      应用程序id
     * @param identifier 标识符
     * @return {@link ResponseVO}
     */
    @RequestMapping("/approveFriendRequest")
    public ResponseVO approveFriendRequest(@RequestBody @Validated
                                                   ApproverFriendRequestReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperater(identifier);
        return imFriendShipRequestService.approverFriendRequest(req);
    }

    /**
     * 查询 添加好友请求
     *
     * @param req   要求事情
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/getFriendRequest")
    public ResponseVO getFriendRequest(@RequestBody @Validated GetFriendShipRequestReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipRequestService.getFriendRequest(req.getFromId(), req.getAppId());
    }

    /**
     * 已读 添加好友亲求
     *
     * @param req   要求事情
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/readFriendShipRequestReq")
    public ResponseVO readFriendShipRequestReq(@RequestBody @Validated ReadFriendShipRequestReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipRequestService.readFriendShipRequestReq(req);
    }


}
