package com.study.im.tcp.receiver;

import com.alibaba.fastjson.JSONObject;
import com.study.im.codec.proto.MessagePack;
import com.study.im.common.ClientType;
import com.study.im.common.constant.Constants;
import com.study.im.common.enums.DeviceMultiLoginEnum;
import com.study.im.common.enums.command.SystemCommand;
import com.study.im.common.model.UserClientDto;
import com.study.im.tcp.redis.RedisManager;
import com.study.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 用户登录消息侦听器  redis 订阅
 * <p>
 * 多端同步： 1.单端登录：一端在线，踢出除了本clientType + imei的设备
 * 2.双端登录：允许 pc/ mobile 其中一端登录 + web端 踢掉除了本 clinetType + imel 以外的web端设备
 * 3 三端登录：允许手机+pc+web，踢掉同端的其他imei 除了web
 * 4 不做任何处理
 *
 * @author lx
 * @date 2023/05/04
 */
public class UserLoginMessageListener {


    private static final Logger logger = LoggerFactory.getLogger(UserLoginMessageListener.class);


    // 多端登录模式，配置文件中配置
    private Integer loginModel;

    public UserLoginMessageListener(Integer loginModel) {
        this.loginModel = loginModel;
    }

    public void listenerUserLogin() {
        RTopic topic = RedisManager.getRedissonClient().getTopic(Constants.RedisConstants.UserLoginChannel);
        topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence charSequence, String msg) {
                logger.info("收到用户上线通知:" + msg);
                UserClientDto userClientDto = JSONObject.parseObject(msg, UserClientDto.class);
                List<NioSocketChannel> userChannels = SessionSocketHolder.getUserChannel(userClientDto.getAppId(), userClientDto.getUserId());
                // 这次登录的 clientType 和 imei
                String clientTypeAndImei = userClientDto.getClientType() + ":" + userClientDto.getImei();

                for (NioSocketChannel nioSocketChannel : userChannels) {
                    Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
                    String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();

                    if (loginModel == DeviceMultiLoginEnum.ONE.getLoginMode()) {
                        // 单端登录 仅允许 Windows、Web、Android 或 iOS 单端登录。
                        if (!(clientType + ":" + imei).equals(clientTypeAndImei)) {
                            // 发送其它端登录的消息
                            sendOtherClientLoginMessage(nioSocketChannel);
                        }

                    } else if (loginModel == DeviceMultiLoginEnum.TWO.getLoginMode()) {
                       // 双端登录 允许 Windows、Mac、Android 或 iOS 单端登录，同时允许与 Web 端同时在线。
                        if (userClientDto.getClientType() == ClientType.WEB.getCode()) {
                            continue;
                        }

                        if (clientType == ClientType.WEB.getCode()) {
                            continue;
                        }
                        if (!(clientType + ":" + imei).equals(clientTypeAndImei)) {
                            sendOtherClientLoginMessage(nioSocketChannel);
                        }

                    } else if (loginModel == DeviceMultiLoginEnum.THREE.getLoginMode()) {
                        // 三端登录 允许 Android 或 iOS 单端登录(互斥)，Windows 或者 Mac 单聊登录(互斥)，同时允许 Web 端同时在线
                        if (userClientDto.getClientType() == ClientType.WEB.getCode()) {
                            continue;
                        }
                        Boolean isSameClient = false;
                        if ((clientType == ClientType.IOS.getCode() || clientType == ClientType.ANDROID.getCode())
                                && (userClientDto.getClientType() == ClientType.IOS.getCode() || userClientDto.getClientType() == ClientType.ANDROID.getCode())) {
                            isSameClient = true;
                        }
                        if ((clientType == ClientType.WINDOWS.getCode() || clientType == ClientType.MAC.getCode())
                                && (userClientDto.getClientType() == ClientType.WINDOWS.getCode() || userClientDto.getClientType() == ClientType.MAC.getCode())) {
                            isSameClient = true;
                        }

                        if (isSameClient && !(clientType + ":" + imei).equals(clientTypeAndImei)) {
                            sendOtherClientLoginMessage(nioSocketChannel);
                        }


                    }

                }
            }
        });

    }

    private void sendOtherClientLoginMessage(NioSocketChannel nioSocketChannel) {
        // 这里不能直接channel.remove() 如果调用remove，这时候正好对应的channel也在发消息，会造成消息丢失
        // 应该 告诉客户端，其他端登录
        MessagePack<Object> pack = new MessagePack<>();
        pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
        pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
        pack.setCommand(SystemCommand.MUTUALLOGIN.getCommand());
        nioSocketChannel.writeAndFlush(pack);
    }
}
