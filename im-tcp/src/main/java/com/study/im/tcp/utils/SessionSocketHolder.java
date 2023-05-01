package com.study.im.tcp.utils;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * channel存储工具类
 *
 * @author lx
 * @date 2023/05/01
 */
public class SessionSocketHolder {

    private static final Map<String, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();


    public static void put(String userId, NioSocketChannel channel) {
        CHANNELS.put(userId, channel);
    }


    public static NioSocketChannel get(String userId) {
        return CHANNELS.get(userId);
    }

}
