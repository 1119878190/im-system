package com.study.im.service.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.study.im.common.BaseErrorCode;
import com.study.im.common.config.AppConfig;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.GateWayErrorCode;
import com.study.im.common.exception.ApplicationExceptionEnum;
import com.study.im.common.utils.SigAPI;
import com.study.im.service.user.service.ImUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class IdentityCheck {

    private static Logger logger = LoggerFactory.getLogger(IdentityCheck.class);


    @Autowired
    private ImUserService imUserService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public ApplicationExceptionEnum checkUserSign(String identifier, String appId, String userSig) {


        // 先看 redis 中是否存在
        String cacheUserSig = stringRedisTemplate.opsForValue()
                .get(appId + ":" + Constants.RedisConstants.userSign + ":"
                        + identifier + userSig);
        if (!StringUtils.isBlank(cacheUserSig) && Long.valueOf(cacheUserSig)
                > System.currentTimeMillis() / 1000) {
            return BaseErrorCode.SUCCESS;
        }


        // 获取密钥
        String privateKey = appConfig.getPrivateKey();

        // 根据appId + 密钥创建sigApi
//        SigAPI sigAPI = new SigAPI(Long.valueOf(appId), privateKey);

        // 调用sigApi对userSig解密
        JSONObject jsonObject = SigAPI.decodeUserSig(userSig);

        // 取出解密后的appid 和 操作人 和 过期时间做匹配，不通过则提示错误
        Long expireTime = 0L;
        Long expireSec = 0L;
        String decoderAppId = "";
        String decoderIdentifier = "";

        try {
            decoderAppId = jsonObject.getString("TLS.appId");
            decoderIdentifier = jsonObject.getString("TLS.identifier");
            String expireStr = jsonObject.get("TLS.expire").toString();
            String expireTimeStr = jsonObject.get("TLS.expireTime").toString();
            expireSec = Long.valueOf(expireStr);
            expireTime = Long.valueOf(expireTimeStr) + expireSec;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("checkUserSig-error:{}", e.getMessage());
        }

        if (!decoderIdentifier.equals(identifier)) {
            return GateWayErrorCode.USERSIGN_OPERATE_NOT_MATE;
        }

        if (!decoderAppId.equals(appId)) {
            return GateWayErrorCode.USERSIGN_IS_ERROR;
        }

        if (expireSec == 0L) {
            return GateWayErrorCode.USERSIGN_IS_EXPIRED;
        }

        if (expireTime < System.currentTimeMillis() / 1000) {
            return GateWayErrorCode.USERSIGN_IS_EXPIRED;
        }


        // 存入 redis 中 appid + "xxx" + userId + sign
        String key = appId + ":" + Constants.RedisConstants.userSign + ":" + identifier + userSig;
        Long etime = expireTime - System.currentTimeMillis() / 1000;
        stringRedisTemplate.opsForValue().set(
                key, expireTime.toString(), etime, TimeUnit.SECONDS
        );
        return BaseErrorCode.SUCCESS;

    }

}
