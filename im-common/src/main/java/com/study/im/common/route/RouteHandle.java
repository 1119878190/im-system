package com.study.im.common.route;

import java.util.List;

/**
 * 路由处理
 *
 * @author lx
 * @date 2023/05/07
 */
public interface RouteHandle {


     /**
      * sdk 路由策略
      *
      * @param values 所有zk节点
      * @param key    键
      * @return {@link String}
      */
     String routeServer(List<String> values,String key);

}
