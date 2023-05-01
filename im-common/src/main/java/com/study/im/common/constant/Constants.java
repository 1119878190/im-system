package com.study.im.common.constant;

import lombok.Data;

/**
 * 常量
 *
 * @author lx
 * @date 2023/05/01
 */
@Data
public class Constants {


    public static class RedisConstants {

        // 用户Session    格式： appid + USER_SESSION_CONSTANT + 用户id
        public static final String USER_SESSION_CONSTANT = ":userSession:";

    }

}
