package com.study.im.tcp.redis;


import com.study.im.codec.config.BootstrapConfig;
import org.redisson.api.RedissonClient;

/**
 * Redis 管理
 *
 *
 * @author: lx
 * @version: 1.0
 */
public class RedisManager {

    private static RedissonClient redissonClient;


    public static void init(BootstrapConfig config) {
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getLim().getRedis());
    }

    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }

}
