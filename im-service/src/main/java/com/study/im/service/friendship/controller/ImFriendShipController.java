package com.study.im.service.friendship.controller;


import com.study.im.common.ResponseVO;
import com.study.im.service.friendship.model.req.*;
import com.study.im.service.friendship.service.ImFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 好友关系controller
 *
 * @author lx
 * @date 2023/04/29
 */
@RestController
@RequestMapping("v1/friendship")
public class ImFriendShipController {

    @Autowired
    ImFriendService imFriendShipService;

    /**
     * 导入好友
     *
     * @param req   要求事情
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/importFriendShip")
    public ResponseVO importFriendShip(@RequestBody @Validated ImportFriendShipReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.importFriendShip(req);
    }

    /**
     * 添加朋友
     *
     * @param req   要求事情
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/addFriend")
    public ResponseVO addFriend(@RequestBody @Validated AddFriendReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.addFriend(req);
    }

    /**
     * 更新朋友
     *
     * @param req   要求事情
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/updateFriend")
    public ResponseVO updateFriend(@RequestBody @Validated UpdateFriendReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.updateFriend(req);
    }

    /**
     * 删除好友
     *
     * @param req   要求事情
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/deleteFriend")
    public ResponseVO deleteFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.deleteFriend(req);
    }

    /**
     * 删除所有好友
     *
     * @param req   要求事情
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/deleteAllFriend")
    public ResponseVO deleteAllFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.deleteAllFriend(req);
    }

    /**
     * 获取所有好友
     *
     * @param req   要求事情
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/getAllFriendShip")
    public ResponseVO getAllFriendShip(@RequestBody @Validated GetAllFriendShipReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.getAllFriendShip(req);
    }

    /**
     * 获取关系
     *
     * @param req   要求事情
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/getRelation")
    public ResponseVO getRelation(@RequestBody @Validated GetRelationReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.getRelation(req);
    }

    /**
     * 校验好友关系
     *
     * @param req   要求事情
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/checkFriend")
    public ResponseVO checkFriend(@RequestBody @Validated CheckFriendShipReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.checkFriendship(req);
    }


    /**
     * 拉黑
     *
     * @param req   亲求参数
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/addBlack")
    public ResponseVO addBlack(@RequestBody @Validated AddFriendShipBlackReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.addBlack(req);
    }

    /**
     * 取消拉黑
     *
     * @param req   亲求参数
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/deleteBlack")
    public ResponseVO deleteBlack(@RequestBody @Validated DeleteBlackReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.deleteBlack(req);
    }

    /**
     * 是否拉黑检查
     *
     * @param req   亲求参数
     * @param appId 应用程序id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/checkBlack")
    public ResponseVO checkBlack(@RequestBody @Validated CheckFriendShipReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.checkBlack(req);
    }


}
