package com.study.im.common.enums.command;

/**
 * 会话事件命令
 *
 * @author lx
 * @date 2023/06/27
 */
public enum ConversationEventCommand implements Command {

    //删除会话
    CONVERSATION_DELETE(5000),

    //删除会话
    CONVERSATION_UPDATE(5001),

    ;

    private int command;

    ConversationEventCommand(int command){
        this.command=command;
    }


    @Override
    public int getCommand() {
        return command;
    }
}
