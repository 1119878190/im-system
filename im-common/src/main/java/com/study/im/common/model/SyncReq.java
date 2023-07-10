package com.study.im.common.model;

import lombok.Data;

/**
 * @author: liux
 * @description: 数据增量同步接口请求
 **/
@Data
public class SyncReq extends RequestBase {

    //客户端最大seq
    private Long lastSequence;
    //一次拉取多少
    private Integer maxLimit;

}
