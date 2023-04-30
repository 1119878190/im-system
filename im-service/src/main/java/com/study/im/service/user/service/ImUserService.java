package com.study.im.service.user.service;


import com.study.im.common.ResponseVO;
import com.study.im.service.user.dao.ImUserDataEntity;
import com.study.im.service.user.model.req.DeleteUserReq;
import com.study.im.service.user.model.req.GetUserInfoReq;
import com.study.im.service.user.model.req.ImportUserReq;
import com.study.im.service.user.model.req.ModifyUserInfoReq;
import com.study.im.service.user.model.resp.GetUserInfoResp;

/**
 * @description:
 * @author: lx
 * @version: 1.0
 */
public interface ImUserService {

    public ResponseVO importUser(ImportUserReq req);

    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req);

    public ResponseVO<ImUserDataEntity> getSingleUserInfo(String userId , Integer appId);

    public ResponseVO deleteUser(DeleteUserReq req);

    public ResponseVO modifyUserInfo(ModifyUserInfoReq req);


}
