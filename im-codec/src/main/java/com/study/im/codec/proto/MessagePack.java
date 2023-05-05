package com.study.im.codec.proto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: lx
 * @description: 消息服务发送给tcp的包体,tcp再根据改包体解析成Message发给客户端
 **/
@Data
public class MessagePack<T> implements Serializable {

    private String userId;
    /**
     * 接收方
     */
    private String toId;

    /**
     * 命令
     */
    private Integer command;


    /**
     * 业务数据对象，如果是聊天消息则不需要解析直接透传
     */
    private T data;



}
