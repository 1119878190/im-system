package com.study.im.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.study.im.codec.message.ChatMessageAck;
import com.study.im.codec.pack.LoginPack;
import com.study.im.codec.proto.Message;
import com.study.im.codec.proto.MessagePack;
import com.study.im.common.ResponseVO;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.ImConnectStatusEnum;
import com.study.im.common.enums.command.MessageCommand;
import com.study.im.common.enums.command.SystemCommand;
import com.study.im.common.model.UserClientDto;
import com.study.im.common.model.UserSession;
import com.study.im.common.model.message.CheckSendMessageReq;
import com.study.im.common.model.message.MessageContent;
import com.study.im.tcp.feign.FeignMessageService;
import com.study.im.tcp.publish.MqMessageProducer;
import com.study.im.tcp.redis.RedisManager;
import com.study.im.tcp.utils.SessionSocketHolder;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
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

    private String logicUrl;

    private FeignMessageService feignMessageService;

    public NettyServerHandler(Integer brokerId, String logicUrl) {
        this.brokerId = brokerId;
        feignMessageService = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                // 设置超时时间
                .options(new Request.Options(1000, 3500))
                .target(FeignMessageService.class, logicUrl);
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

        } else if (command == MessageCommand.MSG_P2P.getCommand()) {
            // 单聊消息
            CheckSendMessageReq req = new CheckSendMessageReq();

            req.setAppId(msg.getMessageHeader().getAppId());
            req.setCommand(msg.getMessageHeader().getCommand());

            MessageContent messageContent = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()), MessageContent.class);
            req.setFromId(messageContent.getFromId());
            req.setToId(messageContent.getToId());

            // 发送消息前置条件，判断是否被禁言，是否好友等
            ResponseVO responseVO = feignMessageService.checkSendMessage(req);
            if (responseVO.isOk()) {
                // 如果成功投递到mq
                MqMessageProducer.sendMessage(msg, command);
            } else {
                // 失败则直接返回ack
                ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
                responseVO.setData(chatMessageAck);
                MessagePack<ResponseVO> ack = new MessagePack<>();
                ack.setData(responseVO);
                ack.setCommand(MessageCommand.MSG_ACK.getCommand());
                ctx.channel().writeAndFlush(ack);
            }

        } else {
            MqMessageProducer.sendMessage(msg, command);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel active");
    }

    //表示 channel 处于不活动状态
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //设置离线
        SessionSocketHolder.offlineUserSession((NioSocketChannel) ctx.channel());
        ctx.close();
    }
}
