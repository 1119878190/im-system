package com.study.im.service.message.service;

import com.study.im.common.ResponseVO;
import com.study.im.common.config.AppConfig;
import com.study.im.common.enums.*;
import com.study.im.service.friendship.dao.ImFriendShipEntity;
import com.study.im.service.friendship.model.req.GetRelationReq;
import com.study.im.service.friendship.service.ImFriendService;
import com.study.im.service.user.dao.ImUserDataEntity;
import com.study.im.service.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 检查发送消息服务
 *
 * @author lx
 * @date 2023/05/19
 */
@Service
public class CheckSendMessageService {

    @Autowired
    private ImUserService imUserService;

    @Autowired
    private ImFriendService imFriendService;

    @Autowired
    private AppConfig appConfig;


    /**
     * 检查发送者是否被禁用或禁言
     *
     * @param fromId 发送方id
     * @param appId  应用程序id
     * @return {@link ResponseVO}
     */
    public ResponseVO checkSenderForbidAndMute(String fromId, Integer appId) {

        ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(fromId, appId);
        if (!singleUserInfo.isOk()) {
            return singleUserInfo;
        }

        ImUserDataEntity user = singleUserInfo.getData();
        if (user.getForbiddenFlag() == UserForbiddenFlagEnum.FORBIBBEN.getCode()) {
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_FORBIDDEN);
        } else if (user.getSilentFlag() == UserSilentFlagEnum.MUTE.getCode()) {
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_MUTE);
        }

        return ResponseVO.successResponse();
    }

    /**
     * 校验好友关系
     *
     * @param fromId 发送方id
     * @param toId   接收方id
     * @param appId  应用程序id
     * @return {@link ResponseVO}
     */
    public ResponseVO checkFriendShip(String fromId, String toId, Integer appId) {

        if (appConfig.isSendMessageCheckFriend()){

            // 双向好友校验
            GetRelationReq fromReq = new GetRelationReq();
            fromReq.setFromId(fromId);
            fromReq.setToId(toId);
            fromReq.setAppId(appId);
            ResponseVO<ImFriendShipEntity> fromRelation = imFriendService.getRelation(fromReq);
            if (!fromRelation.isOk()){
                return fromRelation;
            }

            GetRelationReq toReq = new GetRelationReq();
            toReq.setFromId(toId);
            toReq.setToId(fromId);
            toReq.setAppId(appId);
            ResponseVO<ImFriendShipEntity> toRelation = imFriendService.getRelation(toReq);
            if (!toRelation.isOk()){
                return toRelation;
            }

            // 好友关系状态校验
            if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() != fromRelation.getData().getStatus()){
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
            }
            if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() != toRelation.getData().getStatus()){
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
            }

            if (appConfig.isSendMessageCheckBlack()){
                if (FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode() != fromRelation.getData().getBlack()){
                    return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
                }
                if (FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode() != toRelation.getData().getBlack()){
                    return ResponseVO.errorResponse(FriendShipErrorCode.TARGET_IS_BLACK_YOU);
                }
            }

        }

        return ResponseVO.successResponse();
    }


}
