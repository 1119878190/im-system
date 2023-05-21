package com.study.im.common.enums;

/**
 * 用户禁止标志枚举
 *
 * @author lx
 * @date 2023/05/19
 */
public enum UserForbiddenFlagEnum {

    /**
     * 0 正常；1 禁用。
     */
    NORMAL(0),

    FORBIBBEN(1),
    ;

    private int code;

    UserForbiddenFlagEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
