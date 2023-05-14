package com.study.im.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求基础
 *
 * @author lx
 * @description: 基础请求
 * @date 2023/04/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBase {

    /**
     * 应用程序id
     */
    private Integer appId;

    private String operater;

    /**
     * 客户端类型
     */
    private Integer clientType;

    /**
     * imei
     */
    private String imei;
}
