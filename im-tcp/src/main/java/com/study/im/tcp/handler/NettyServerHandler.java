package com.study.im.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.study.im.codec.pack.LoginPack;
import com.study.im.codec.proto.Message;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.ImConnectStatusEnum;
import com.study.im.common.enums.command.SystemCommand;
import com.study.im.common.model.UserSession;
import com.study.im.tcp.redis.RedisManager;
import com.study.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
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


            // 将用户信息存储到 redis map 中
            UserSession userSession = new UserSession();
            userSession.setUserId(loginPack.getUserId());
            userSession.setAppId(msg.getMessageHeader().getAppId());
            userSession.setClientType(msg.getMessageHeader().getClientType());
            userSession.setConnectState(ImConnectStatusEnum.ONLINE_STATUS.getCode());

            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(msg.getMessageHeader().getAppId() + Constants.RedisConstants.USER_SESSION_CONSTANT + loginPack.getUserId());
            map.put(String.valueOf(msg.getMessageHeader().getClientType()), JSONObject.toJSONString(userSession));


            // 将 channel 存起来
            SessionSocketHolder.put(loginPack.getUserId(), (NioSocketChannel) ctx.channel());

        }


    }
}
