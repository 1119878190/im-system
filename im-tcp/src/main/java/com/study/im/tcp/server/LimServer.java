package com.study.im.tcp.server;

import com.study.im.codec.config.BootstrapConfig;
import com.study.im.codec.config.MessageDecoder;
import com.study.im.tcp.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * im服务器
 *
 * @author lx
 * @date 2023/04/30
 */
public class LimServer {

    private static final Logger logger = LoggerFactory.getLogger(LimServer.class);

    EventLoopGroup mainGroup;
    EventLoopGroup subGroup;
    ServerBootstrap serverBootstrap;
    BootstrapConfig.TcpConfig tcpConfig;

    public LimServer(BootstrapConfig.TcpConfig tcpConfig) {
        this.tcpConfig = tcpConfig;
        mainGroup = new NioEventLoopGroup(tcpConfig.getBossThreadSize());
        subGroup = new NioEventLoopGroup(tcpConfig.getWorkThreadSize());
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
                // 服务端可连接队列大小
                .option(ChannelOption.SO_BACKLOG, 10240)
                // 参数表示允许重复使用本地地址和端口
                .option(ChannelOption.SO_REUSEADDR, true)
                // 是否禁用Nagle算法 简单点说是否批量发送数据 true关闭 false开启。 开启的话可以减少一定的网络开销，但影响消息实时性
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 保活开关2h没有数据服务端会发送心跳包
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MessageDecoder());
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });

        logger.info("LimServer 启动成功port:[{}]", tcpConfig.getTcpPort());
    }

    public void start() {
        this.serverBootstrap.bind(tcpConfig.getTcpPort());

    }

}
