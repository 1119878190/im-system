package com.study.im.codec.config;


import lombok.Data;

/**
 * 引导配置
 *
 * @author lx
 * @date 2023/04/30
 */
@Data
public class BootstrapConfig {

    private TcpConfig lim;


    @Data
    public static class TcpConfig {
        private int tcpPort;
        private int webSocketPort;
        private int bossThreadSize;
        private int workThreadSize;
    }


}
