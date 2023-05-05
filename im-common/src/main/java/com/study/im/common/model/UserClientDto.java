package com.study.im.common.model;

import lombok.Data;

/**
 * 用户存放登录Session的Key
 *
 * @description:
 * @author: lx
 * @version: 1.0
 */
@Data
public class UserClientDto {

    private Integer appId;

    private Integer clientType;

    private String userId;

    private String imei;


}
