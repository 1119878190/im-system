package com.study.im.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用程序配置
 *
 * @author lx
 * @date 2023/05/07
 */
@Data
@Component
@ConfigurationProperties(prefix = "appconfig")
public class AppConfig {


    /**
     * zookeeper连接地址
     */
    private String zkAddr;

    /**
     * zk连接超时
     */
    private Integer zkConnectTimeOut;

    /**
     * im路由策略
     */
    private Integer imRouteWay;

    /**
     * 一致性哈希算法
     */
    private Integer consistentHashWay;


}
