package com.study.im.codec.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 引导配置
 *
 * @author lx
 * @date 2023/04/30
 */
@Data
public class BootstrapConfig {

    private TcpConfig lim;


    @Data
    public static class TcpConfig {
        /**
         * tcp 绑定的端口号
         */
        private int tcpPort;
        /**
         * websocket 绑定的端口号
         */
        private int webSocketPort;
        /**
         *  netty 负责连接的线程数
         */
        private int bossThreadSize;
        /**
         * netty 负责工作的线程数
         */
        private int workThreadSize;
        /**
         * 心跳超时时间  单位毫秒
         */
        private long heartBeatTime;

        /**
         * redis配置
         */
        private RedisConfig redis;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisConfig {

        /**
         * 单机模式：single 哨兵模式：sentinel 集群模式：cluster
         */
        private String mode;
        /**
         * 数据库
         */
        private Integer database;
        /**
         * 密码
         */
        private String password;
        /**
         * 超时时间
         */
        private Integer timeout;
        /**
         * 最小空闲数
         */
        private Integer poolMinIdle;
        /**
         * 连接超时时间(毫秒)
         */
        private Integer poolConnTimeout;
        /**
         * 连接池大小
         */
        private Integer poolSize;

        /**
         * redis单机配置
         */
        private RedisSingle single;

    }


    /**
     * redis单机配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisSingle {
        /**
         * 地址
         */
        private String address;
    }
}
