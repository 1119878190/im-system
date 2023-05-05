package com.study.im.tcp.redis;


import com.study.im.codec.config.BootstrapConfig;
import com.study.im.tcp.receiver.UserLoginMessageListener;
import org.redisson.api.RedissonClient;

/**
 * Redis 管理
 *
 * @author: lx
 * @version: 1.0
 */
public class RedisManager {

    private static RedissonClient redissonClient;


    public static void init(BootstrapConfig config) {
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getLim().getRedis());

        // 多端登录 redis 订阅监听
        UserLoginMessageListener userLoginMessageListener = new UserLoginMessageListener(config.getLim().getLoginModel());
        userLoginMessageListener.listenerUserLogin();
    }

    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }

}
