package com.study.im.common.model;

import lombok.Data;

/**
 * 请求基础
 *
 * @author lx
 * @description: 基础请求
 * @date 2023/04/29
 */
@Data
public class RequestBase {
    private Integer appId;

    private String operater;

    private Integer clientType;

    private String imei;
}
