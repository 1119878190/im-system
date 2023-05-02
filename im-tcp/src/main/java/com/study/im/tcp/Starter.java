package com.study.im.tcp;


import com.study.im.codec.config.BootstrapConfig;
import com.study.im.tcp.receiver.MqMessageReceiver;
import com.study.im.tcp.redis.RedisManager;
import com.study.im.tcp.register.RegisterZK;
import com.study.im.tcp.register.ZKit;
import com.study.im.tcp.server.LimServer;
import com.study.im.tcp.server.LimWebsocketServer;
import com.study.im.tcp.utils.MqFactory;
import org.I0Itec.zkclient.ZkClient;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 起动器
 *
 * @author lx
 * @date 2023/04/30
 */
public class Starter {

    public static void main(String[] args) {

        if (args.length > 0){
            start(args[0]);
        }
    }


    public static void start(String path) {
        Yaml yaml = new Yaml();
        try {
            FileInputStream inputStream = new FileInputStream(path);
            BootstrapConfig bootstrapConfig = yaml.loadAs(inputStream, BootstrapConfig.class);

            new LimServer(bootstrapConfig.getLim()).start();
            new LimWebsocketServer(bootstrapConfig.getLim()).start();

            // redis
            RedisManager.init(bootstrapConfig);
            // mq
            MqFactory.init(bootstrapConfig.getLim().getRabbitmq());
            MqMessageReceiver.init();
            // zk
            registerZK(bootstrapConfig);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


    public static void registerZK(BootstrapConfig config){
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            ZkClient zkClient = new ZkClient(config.getLim().getZkConfig().getZkAddr(), config.getLim().getZkConfig().getZkConnectTimeOut());
            ZKit zKit = new ZKit(zkClient);
            RegisterZK registerZK = new RegisterZK(zKit, hostAddress, config.getLim());
            Thread thread = new Thread(registerZK);
            thread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }

}
