package com.study.im.service.config;


import com.study.im.common.config.AppConfig;
import com.study.im.common.route.RouteHandle;
import com.study.im.common.route.algorithm.loop.LoopHandle;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * bean配置
 *
 * @author lx
 * @date 2023/05/07
 */
@Configuration
public class BeanConfig {


    @Autowired
    private AppConfig appConfig;

    @Bean
    public RouteHandle routeHandle() {
        return new LoopHandle();
    }

    @Bean
    public ZkClient buildZiClient() {
        return new ZkClient(appConfig.getZkAddr(), appConfig.getZkConnectTimeOut());
    }

}
