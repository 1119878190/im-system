package com.study.im.tcp.receiver.process;


import com.study.im.codec.proto.MessagePack;
import com.study.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
public abstract class BaseProcess {

    public abstract void processBefore();

    public void process(MessagePack messagePack){
        processBefore();
        // 这里直接通过缓存map拿到对应的channel
        // (可以拿到channel是因为业务服务在发送mq消息的时候，通过路由层redis获取到了设备对应的brokerId，mq发送消息的时候的路由为对应的brokerId)
        NioSocketChannel channel = SessionSocketHolder.get(messagePack.getAppId(),
                messagePack.getToId(), messagePack.getClientType(),
                messagePack.getImei());
        if(channel != null){
            channel.writeAndFlush(messagePack);
        }
        processAfter();
    }

    public abstract void processAfter();

}
