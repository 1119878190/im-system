package com.study.im.service.group.service;

import com.study.im.common.ResponseVO;
import com.study.im.service.group.dao.ImGroupEntity;
import com.study.im.service.group.model.req.*;

/**
 * @description:
 * @author: lx
 * @version: 1.0
 */
public interface ImGroupService {

    /**
     * 导入群组
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO importGroup(ImportGroupReq req);

    /**
     * 创建群组
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO createGroup(CreateGroupReq req);

    /**
     * 更新群组基本信息
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO updateBaseGroupInfo(UpdateGroupReq req);

    /**
     * 获取用户加入的所有群
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO getJoinedGroup(GetJoinedGroupReq req);

    /**
     * 解散群
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO destroyGroup(DestroyGroupReq req);

    /**
     * 转让群
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO transferGroup(TransferGroupReq req);

    /**
     * 查询群信息
     *
     * @param groupId 组id
     * @param appId   应用程序id
     * @return {@link ResponseVO}<{@link ImGroupEntity}>
     */
    ResponseVO<ImGroupEntity> getGroup(String groupId, Integer appId);

    /**
     * 查询群信息--带有成员
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO getGroup(GetGroupReq req);

    /**
     * 禁言群
     *
     * @param req 请求参数
     * @return {@link ResponseVO}
     */
    ResponseVO muteGroup(MuteGroupReq req);

}
