package com.study.im.common.enums.command;

public enum MessageCommand implements Command {


    // 单聊消息1103
    MSG_P2P(0X44F),

    //单聊消息ACK 1046
    MSG_ACK(0x416),

    ;

    private int command;

    MessageCommand(int command) {
        this.command = command;
    }


    @Override
    public int getCommand() {
        return command;
    }

}