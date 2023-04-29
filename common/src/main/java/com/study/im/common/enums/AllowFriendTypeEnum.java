package com.study.im.common.enums;

/**
 * 添加好友是否需要申请
 *
 * @author lx
 * @date 2023/04/29
 */
public enum AllowFriendTypeEnum {

    /**
     * 验证
     */
    NEED(2),

    /**
     * 不需要验证
     */
    NOT_NEED(1),

    ;


    private int code;

    AllowFriendTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
