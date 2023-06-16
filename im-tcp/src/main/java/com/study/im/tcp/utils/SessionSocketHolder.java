package com.study.im.tcp.utils;

import com.alibaba.fastjson.JSONObject;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.ImConnectStatusEnum;
import com.study.im.common.model.UserClientDto;
import com.study.im.common.model.UserSession;
import com.study.im.tcp.redis.RedisManager;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * channel存储工具类
 *
 * @author lx
 * @date 2023/05/01
 */
public class SessionSocketHolder {

    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();


    public static void put(Integer appId, String userId, Integer clientType, String imei, NioSocketChannel channel) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        dto.setImei(imei);
        CHANNELS.put(dto, channel);
    }

    public static NioSocketChannel get(Integer appId, String userId,
                                       Integer clientType, String imei) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        dto.setImei(imei);
        return CHANNELS.get(dto);
    }


    public static void remove(Integer appId, String userId, Integer clientType, String imei) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        dto.setImei(imei);
        CHANNELS.remove(dto);
    }

    public static void remove(NioSocketChannel channel) {
        CHANNELS.entrySet().stream().filter(entity -> entity.getValue() == channel)
                .forEach(entry -> CHANNELS.remove(entry.getKey()));
    }


    /**
     * 用户主动退出
     *
     * @param nioSocketChannel nio套接字通道
     */
    public static void removeUserSession(NioSocketChannel nioSocketChannel) {
        // 用户退出
        String userId = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get();
        Integer appId = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.AppId)).get();
        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
        String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();


        // 删除 session
        SessionSocketHolder.remove(appId, userId, clientType, imei);

        // 删除 redis 用户信息
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<Object, Object> map = redissonClient.getMap(appId + Constants.RedisConstants.USER_SESSION_CONSTANT + userId);
        map.remove(clientType + ":" + imei);
        nioSocketChannel.close();
    }


    /**
     * 离线用户会话
     * (退后台，或长时间没有发送消息)
     *
     * @param nioSocketChannel nio套接字通道
     */
    public static void offlineUserSession(NioSocketChannel nioSocketChannel) {
        // 用户退出
        String userId = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get();
        Integer appId = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.AppId)).get();
        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
        String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();

        // 删除 session
        SessionSocketHolder.remove(appId, userId, clientType, imei);

        // 设置为离线状态
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId + Constants.RedisConstants.USER_SESSION_CONSTANT + userId);
        String sessionStr = map.get(clientType.toString() + ":" + imei);

        if (StringUtils.isNotBlank(sessionStr)) {
            UserSession userSession = JSONObject.parseObject(sessionStr, UserSession.class);
            userSession.setConnectState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            map.put(clientType.toString() + ":" + imei, JSONObject.toJSONString(userSession));
        }

        nioSocketChannel.close();
    }


    /**
     * 获取指定用户所有端的channel
     *
     * @param appId  应用程序id
     * @param userId 用户id
     * @return {@link List}<{@link NioSocketChannel}>
     */
    public static List<NioSocketChannel> getUserChannel(Integer appId, String userId) {
        Set<UserClientDto> channelInfos = CHANNELS.keySet();
        List<NioSocketChannel> channels = new ArrayList<>();
        for (UserClientDto channelInfo : channelInfos) {
            if (channelInfo.getAppId().equals(appId) && channelInfo.getUserId().equals(userId)) {
                channels.add(CHANNELS.get(channelInfo));
            }
        }
        return channels;
    }


}
