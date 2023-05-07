package com.study.im.service.utils;

import com.alibaba.fastjson.JSON;
import com.study.im.common.constant.Constants;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Zookeeper 工具
 *
 * @author lx
 * @date 2023/05/07
 */
@Component
public class ZKit {

    private static Logger logger = LoggerFactory.getLogger(ZKit.class);

    @Autowired
    private ZkClient zkClient;

    /**
     * 得到所有tcp节点
     *
     * @return {@link List}<{@link String}>
     */
    public List<String> getAllTcpNode(){
        List<String> children = zkClient.getChildren(Constants.ImCoreZkRoot + Constants.ImCoreZkRootTcp);
        logger.info("获取到所有tcp节点:"+ JSON.toJSONString(children));
        return children;
    }


    /**
     * 得到所有 web 节点
     *
     * @return {@link List}<{@link String}>
     */
    public List<String> getAllWebNode() {
        List<String> children = zkClient.getChildren(Constants.ImCoreZkRoot + Constants.ImCoreZkRootWeb);
        logger.info("获取到所有web节点:"+ JSON.toJSONString(children));
        return children;
    }

}
