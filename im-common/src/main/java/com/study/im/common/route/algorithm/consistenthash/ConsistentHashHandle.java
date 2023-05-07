package com.study.im.common.route.algorithm.consistenthash;

import com.study.im.common.route.RouteHandle;

import java.util.List;

/**
 * 负载均衡--一致性哈希处理
 *
 * @author lx
 * @date 2023/05/08
 */
public class ConsistentHashHandle implements RouteHandle {

    private AbstractConsistentHash hash;


    public void setHash(AbstractConsistentHash hash) {
        this.hash = hash;
    }

    @Override
    public String routeServer(List<String> nodes, String key) {
        return hash.process(nodes, key);
    }
}
