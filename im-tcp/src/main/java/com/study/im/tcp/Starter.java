package com.study.im.tcp;


import com.study.im.codec.config.BootstrapConfig;
import com.study.im.tcp.server.LimServer;
import com.study.im.tcp.server.LimWebsocketServer;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 起动器
 *
 * @author lx
 * @date 2023/04/30
 */
public class Starter {

    public static void main(String[] args) {

        start("E:\\developCode\\IM\\im-system\\im-tcp\\src\\main\\resources\\application.yml");
    }


    public static void start(String path){
        Yaml yaml = new Yaml();
        try {
            FileInputStream inputStream = new FileInputStream(path);
            BootstrapConfig bootstrapConfig = yaml.loadAs(inputStream, BootstrapConfig.class);
            new LimServer(bootstrapConfig.getLim()).start();
            new LimWebsocketServer(bootstrapConfig.getLim()).start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


}
