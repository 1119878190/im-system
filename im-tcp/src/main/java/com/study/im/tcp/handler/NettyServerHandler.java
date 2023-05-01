package com.study.im.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.study.im.codec.pack.LoginPack;
import com.study.im.codec.proto.Message;
import com.study.im.common.enums.command.SystemCommand;
import com.study.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 业务处理handler
 *
 * @author lx
 * @date 2023/05/01
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        Integer command = msg.getMessageHeader().getCommand();

        // 登录 command
        if (command == SystemCommand.LOGIN.getCommand()) {
            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()), LoginPack.class);

            ctx.channel().attr(AttributeKey.valueOf("userId")).set(loginPack.getUserId());

            // 将 channel 存起来
            SessionSocketHolder.put(loginPack.getUserId(), (NioSocketChannel) ctx.channel());

        }


    }
}
