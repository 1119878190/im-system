package com.study.im.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author lx
 * @date 2023/04/29
 */
@SpringBootApplication(scanBasePackages = {"com.study.im.service","com.study.im.common"})
public class IMApplication {

    public static void main(String[] args) {
        SpringApplication.run(IMApplication.class, args);
    }
}
