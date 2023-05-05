package com.study.im.common.enums.command;

/**
 * 消息系统命令
 *
 * @author lx
 * @date 2023/05/01
 */
public enum SystemCommand implements Command {


    /**
     * 心跳 9999
     */
    PING(0x270f),


    /**
     * 登录 9000
     */
    LOGIN(0x2328),


    /**
     * 登出  9003
     */
    LOGOUT(0x232b),


    /**
     * 下线通知 用于多端互斥  9002
     */
    MUTUALLOGIN(0x232a),


    ;


    private int command;

    SystemCommand(int command) {
        this.command = command;
    }


    @Override
    public int getCommand() {
        return command;
    }
}
