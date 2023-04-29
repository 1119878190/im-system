package com.study.im.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: lx
 * @description:
 **/
@Data
@NoArgsConstructor
public class ClientInfo {

    private Integer appId;

    private Integer clientType;

    private String imei;

    public ClientInfo(Integer appId, Integer clientType, String imei) {
        this.appId = appId;
        this.clientType = clientType;
        this.imei = imei;
    }
}
