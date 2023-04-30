package com.study.im.service.friendship.controller;


import com.study.im.common.ResponseVO;
import com.study.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.study.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.study.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.study.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.study.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.study.im.service.friendship.service.ImFriendShipGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: lx
 * @description:
 **/
@RestController
@RequestMapping("v1/friendship/group")
public class ImFriendShipGroupController {

    @Autowired
    ImFriendShipGroupService imFriendShipGroupService;

    @Autowired
    ImFriendShipGroupMemberService imFriendShipGroupMemberService;


    /**
     * 添加好友分组
     *
     * @param req   请求参数
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/add")
    public ResponseVO add(@RequestBody @Validated AddFriendShipGroupReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupService.addGroup(req);
    }

    /**
     * 删除好友分组
     *
     * @param req   请求参数
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/del")
    public ResponseVO del(@RequestBody @Validated DeleteFriendShipGroupReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupService.deleteGroup(req);
    }

    /**
     * 往好友分组中添加好友
     *
     * @param req   请求参数
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/member/add")
    public ResponseVO memberAdd(@RequestBody @Validated AddFriendShipGroupMemberReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupMemberService.addGroupMember(req);
    }

    /**
     * 移除好友分组中的人
     *
     * @param req   请求参数
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/member/del")
    public ResponseVO memberdel(@RequestBody @Validated DeleteFriendShipGroupMemberReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupMemberService.delGroupMember(req);
    }


}
