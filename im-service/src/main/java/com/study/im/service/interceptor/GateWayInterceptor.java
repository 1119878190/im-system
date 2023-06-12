package com.study.im.service.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.study.im.common.BaseErrorCode;
import com.study.im.common.ResponseVO;
import com.study.im.common.enums.GateWayErrorCode;
import com.study.im.common.exception.ApplicationExceptionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 拦截器
 *
 * @author lx
 * @date 2023/05/14
 */
@Component
public class GateWayInterceptor implements HandlerInterceptor {


    @Autowired
    private IdentityCheck identityCheck;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (1 == 1) {
            return true;
        }


        // 获取appId，操作人， userSign
        String appIdStr = request.getParameter("appId");
        if (StringUtils.isBlank(appIdStr)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode.APPID_NOT_EXIST), response);
            return false;
        }

        String identifier = request.getParameter("identifier");
        if (StringUtils.isBlank(identifier)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode.OPERATER_NOT_EXIST), response);
            return false;
        }

        String userSign = request.getParameter("userSign");
        if (StringUtils.isBlank(userSign)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode.USERSIGN_NOT_EXIST), response);
            return false;
        }


        // 签名，操作人，appId 是否匹配
        ApplicationExceptionEnum applicationExceptionEnum = identityCheck.checkUserSign(identifier, appIdStr, userSign);
        if (applicationExceptionEnum != BaseErrorCode.SUCCESS) {
            resp(ResponseVO.errorResponse(applicationExceptionEnum), response);
            return false;
        }
        return true;

    }


    private void resp(ResponseVO respVO, HttpServletResponse response) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            String resp = JSONObject.toJSONString(respVO);
            writer = response.getWriter();
            writer.write(resp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.checkError();
            }
        }


    }


}
