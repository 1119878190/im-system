package com.study.im.service.utils;


import com.alibaba.fastjson.JSONObject;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.ImConnectStatusEnum;
import com.study.im.common.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户会话工具类
 *
 * @author lx
 * @date 2023/05/10
 */
@Component
public class UserSessionUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 获取用户所有的在线session
     *
     * @param appId  应用程序id
     * @param userId 用户id
     * @return {@link List}<{@link UserSession}>
     */
    public List<UserSession> getUserSession(Integer appId,String userId){
        String userSessionKey = appId + Constants.RedisConstants.USER_SESSION_CONSTANT + userId;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(userSessionKey);
        List<UserSession> list = new ArrayList<>();
        Collection<Object> values = entries.values();
        for (Object value : values) {
            String str = (String) value;
            UserSession userSession = JSONObject.parseObject(str, UserSession.class);
            if (userSession.getConnectState().equals(ImConnectStatusEnum.ONLINE_STATUS.getCode())){
                list.add(userSession);
            }

        }
        return list;

    }





    /**
     * 获取指定端的用户session
     *
     * @param appId  应用程序id
     * @param userId 用户id
     * @return {@link List}<{@link UserSession}>
     */
    public UserSession getUserSession(Integer appId,String userId,Integer clientType ,String imei){
        String userSessionKey = appId + Constants.RedisConstants.USER_SESSION_CONSTANT + userId;
        String hashKey = clientType + ":" + imei;
        Object o = stringRedisTemplate.opsForHash().get(userSessionKey, hashKey);
        UserSession userSession = JSONObject.parseObject(o.toString(), UserSession.class);
        return userSession;

    }





}
