package com.study.im.common.model;

import lombok.Data;

/**
 * @description:
 * @author: lx
 * @version: 1.0
 */
@Data
public class RequestBase {
    private Integer appId;

    private String operater;

    private Integer clientType;

    private String imei;
}
