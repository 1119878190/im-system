package com.study.im.common.enums.command;

/**
 * 消息命令
 *
 * @author lx
 * @date 2023/06/25
 */
public enum MessageCommand implements Command {


    // 单聊消息1103
    MSG_P2P(0X44F),

    //单聊消息ACK 1046
    MSG_ACK(0x416),

    //消息收到ack 1107
    MSG_RECEIVE_ACK(1107),

    //发送消息已读--由client发送(消息接收方设备)   1106
    MSG_READED(0x452),

    //消息已读通知给同步端--由client发送(用于告知消息接收方其它在线设备) 1053
    MSG_READED_NOTIFY(0x41D),

    //消息已读回执，给原消息发送方 1054
    MSG_READED_RECEIPT(0x41E),

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
