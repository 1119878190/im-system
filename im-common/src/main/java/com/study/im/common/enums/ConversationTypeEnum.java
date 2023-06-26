package com.study.im.common.enums;

/**
 * 会话枚举类型
 *
 * @author lx
 * @date 2023/06/26
 */
public enum ConversationTypeEnum {

    /**
     * 0 单聊 1群聊 2机器人 3公众号
     */
    P2P(0),

    GROUP(1),

    ROBOT(2),
    ;

    private int code;

    ConversationTypeEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
