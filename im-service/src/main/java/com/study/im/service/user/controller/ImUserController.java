package com.study.im.service.user.controller;


import com.study.im.common.ClientType;
import com.study.im.common.ResponseVO;
import com.study.im.common.route.RouteHandle;
import com.study.im.common.route.RouteInfo;
import com.study.im.common.utils.RouteInfoParseUtil;
import com.study.im.service.user.model.req.DeleteUserReq;
import com.study.im.service.user.model.req.ImportUserReq;
import com.study.im.service.user.model.req.LoginReq;
import com.study.im.service.user.service.ImUserService;
import com.study.im.service.utils.ZKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @description:
 * @author: lx
 * @version: 1.0
 */
@RestController
@RequestMapping("v1/user")
public class ImUserController {
    @Autowired
    ImUserService imUserService;

    @Autowired
    private RouteHandle routeHandle;

    @Autowired
    private ZKit zKit;

    @RequestMapping("importUser")
    public ResponseVO importUser(@RequestBody ImportUserReq req, Integer appId) {
        return imUserService.importUser(req);
    }

    @RequestMapping("/deleteUser")
    public ResponseVO deleteUser(@RequestBody @Validated DeleteUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.deleteUser(req);
    }


    /**
     * @param req
     * @return com.lld.im.common.ResponseVO
     * @description im的登录接口，返回im地址
     * @author chackylee
     */
    @RequestMapping("/login")
    public ResponseVO login(@RequestBody @Validated LoginReq req, Integer appId) {
        req.setAppId(appId);

        ResponseVO login = imUserService.login(req);
        if (login.isOk()) {

            List<String> allNode;
            if (req.getClientType() == ClientType.WEB.getCode()) {
                allNode = zKit.getAllTcpNode();
            } else {
                allNode = zKit.getAllWebNode();
            }
            String ipPort = routeHandle.routeServer(allNode, req.getUserId());
            RouteInfo parse = RouteInfoParseUtil.parse(ipPort);
            return ResponseVO.successResponse(parse);
        }


        return ResponseVO.errorResponse();
    }

}
