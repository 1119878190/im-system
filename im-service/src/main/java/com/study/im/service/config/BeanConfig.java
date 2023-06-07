package com.study.im.service.config;


import com.study.im.common.config.AppConfig;
import com.study.im.common.enums.ImUrlRouteWayEnum;
import com.study.im.common.enums.RouteHashMethodEnum;
import com.study.im.common.route.RouteHandle;
import com.study.im.common.route.algorithm.consistenthash.AbstractConsistentHash;
import com.study.im.service.utils.SnowflakeIdWorker;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

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


        Integer imRouteWay = appConfig.getImRouteWay();
        ImUrlRouteWayEnum handler = ImUrlRouteWayEnum.getHandler(imRouteWay);
        String clazz = handler.getClazz();
        try {
            RouteHandle routeHandle = (RouteHandle) Class.forName(clazz).newInstance();
            if (handler == ImUrlRouteWayEnum.HASH) {
                Method method = Class.forName(clazz).getMethod("setHash", AbstractConsistentHash.class);

                Integer consistentHashWay = appConfig.getConsistentHashWay();
                String hashWay = RouteHashMethodEnum.getHandler(consistentHashWay).getClazz();

                AbstractConsistentHash consistentHash = (AbstractConsistentHash) Class.forName(hashWay).newInstance();
                method.invoke(routeHandle, consistentHash);

            }
            return routeHandle;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Bean
    public ZkClient buildZiClient() {
        return new ZkClient(appConfig.getZkAddr(), appConfig.getZkConnectTimeOut());
    }


    /**
     * 向mybatis-plus添加自定义通用方法
     *
     * @return {@link EasySqlInjector}
     */
    @Bean
    public EasySqlInjector easySqlInjector () {
        return new EasySqlInjector();
    }


    /**
     * 雪花算法
     *
     * @return {@link SnowflakeIdWorker}
     * @throws Exception 异常
     */
    @Bean
    public SnowflakeIdWorker buildSnowflakeSeq() throws Exception {
        return new SnowflakeIdWorker(0);
    }
}
