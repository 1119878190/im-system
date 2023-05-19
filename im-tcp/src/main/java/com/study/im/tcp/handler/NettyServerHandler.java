package com.study.im.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.study.im.codec.pack.LoginPack;
import com.study.im.codec.proto.Message;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.ImConnectStatusEnum;
import com.study.im.common.enums.command.SystemCommand;
import com.study.im.common.model.UserClientDto;
import com.study.im.common.model.UserSession;
import com.study.im.tcp.publish.MqMessageProducer;
import com.study.im.tcp.redis.RedisManager;
import com.study.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * 业务处理handler
 *
 * @author lx
 * @date 2023/05/01
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private Integer brokerId;

    public NettyServerHandler(Integer brokerId) {
        this.brokerId = brokerId;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        Integer command = msg.getMessageHeader().getCommand();

        // 登录 command
        if (command == SystemCommand.LOGIN.getCommand()) {
            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()), LoginPack.class);

            ctx.channel().attr(AttributeKey.valueOf(Constants.UserId)).set(loginPack.getUserId());
            ctx.channel().attr(AttributeKey.valueOf(Constants.AppId)).set(msg.getMessageHeader().getAppId());
            ctx.channel().attr(AttributeKey.valueOf(Constants.ClientType)).set(msg.getMessageHeader().getClientType());
            ctx.channel().attr(AttributeKey.valueOf(Constants.Imei)).set(msg.getMessageHeader().getImei());

            // 将用户信息存储到 redis map 中
            UserSession userSession = new UserSession();
            userSession.setUserId(loginPack.getUserId());
            userSession.setAppId(msg.getMessageHeader().getAppId());
            userSession.setClientType(msg.getMessageHeader().getClientType());
            userSession.setConnectState(ImConnectStatusEnum.ONLINE_STATUS.getCode());
            userSession.setBrokerId(brokerId);
            userSession.setImei(msg.getMessageHeader().getImei());

            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            userSession.setBrokerHost(hostAddress);


            // appId:userSession:usrId    clientType:imei   usrSession
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(msg.getMessageHeader().getAppId() + Constants.RedisConstants.USER_SESSION_CONSTANT + loginPack.getUserId());
            map.put(String.valueOf(msg.getMessageHeader().getClientType()) + ":" + msg.getMessageHeader().getImei(),
                    JSONObject.toJSONString(userSession));

            // 将 channel 存起来
            SessionSocketHolder.put(msg.getMessageHeader().getAppId(),
                    loginPack.getUserId(),
                    msg.getMessageHeader().getClientType(),
                    msg.getMessageHeader().getImei(),
                    (NioSocketChannel) ctx.channel());

            // 多端登录，通过redis的发布订阅，通知其它服务上的端进行退出
            UserClientDto userClientDto = new UserClientDto();
            userClientDto.setImei(msg.getMessageHeader().getImei());
            userClientDto.setUserId(loginPack.getUserId());
            userClientDto.setClientType(msg.getMessageHeader().getClientType());
            userClientDto.setAppId(msg.getMessageHeader().getAppId());
            RTopic topic = redissonClient.getTopic(Constants.RedisConstants.UserLoginChannel);
            topic.publish(JSONObject.toJSONString(userClientDto));

        } else if (command == SystemCommand.LOGOUT.getCommand()) {
            // 用户退出
            SessionSocketHolder.removeUserSession((NioSocketChannel) ctx.channel());

        } else if (command == SystemCommand.PING.getCommand()) {
            // 心跳检测事件
            // 设置读写时间时间
            ctx.channel().attr(AttributeKey.valueOf(Constants.ReadTime)).set(System.currentTimeMillis());

        } else {
            MqMessageProducer.sendMessage(msg, command);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel active");
    }
}
