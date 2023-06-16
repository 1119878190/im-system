package com.study.im.service.utils;


/**
 * redis 消息递增标识生成规则
 *
 * @author: lx
 *
 **/
public class ConversationIdGenerate {


    /**
     * 生成p2 seq
     *
     * 发送方和接收方 ，哪个id大哪个在前面
     *
     * @param fromId 发送方id
     * @param toId   接收方id
     * @return {@link String}
     */
    public static String generateP2PId(String fromId,String toId){
        int i = fromId.compareTo(toId);
        if(i < 0){
            return toId+"|"+fromId;
        }else if(i > 0){
            return fromId+"|"+toId;
        }

        throw new RuntimeException("");
    }
}
