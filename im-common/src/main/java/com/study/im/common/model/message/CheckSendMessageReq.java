package com.study.im.common.model.message;

import lombok.Data;

/**
 * 检查发送信息req
 *
 * @author lx
 * @date 2023/06/12
 */
@Data
public class CheckSendMessageReq {

    private String fromId;

    private String toId;

    private Integer appId;

    private Integer command;
}
