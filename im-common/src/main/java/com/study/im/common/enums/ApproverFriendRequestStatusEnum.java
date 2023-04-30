package com.study.im.common.enums;

/**
 * 好友请求枚状态枚举
 *
 * @author lx
 * @date 2023/04/29
 */
public enum ApproverFriendRequestStatusEnum {

    /**
     * 1 同意；2 拒绝。
     */
    AGREE(1),

    REJECT(2),
    ;

    private int code;

    ApproverFriendRequestStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
