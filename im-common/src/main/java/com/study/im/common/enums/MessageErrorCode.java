package com.study.im.common.enums;

import com.study.im.common.exception.ApplicationExceptionEnum;

/**
 * 好友关系状态枚举
 *
 * @author lx
 * @date 2023/04/29
 */
public enum MessageErrorCode implements ApplicationExceptionEnum {


    FROMER_IS_MUTE(50002,"发送方被禁言"),

    FROMER_IS_FORBIDDEN(50003,"发送方被禁用"),




    ;

    private int code;
    private String error;

    MessageErrorCode(int code, String error){
        this.code = code;
        this.error = error;
    }
    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getError() {
        return this.error;
    }

}
