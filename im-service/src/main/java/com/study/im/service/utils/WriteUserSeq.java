package com.study.im.service.utils;

import com.study.im.common.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 写用户同步seq
 *
 * @author lx
 * @date 2023/07/08
 */
@Service
public class WriteUserSeq {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 写用户seq，使用redis的hash结构
     * uid  friend 10
     * group 12
     * conversation 123
     *
     * @param appId  应用程序id
     * @param userId 用户id
     * @param type   类型
     * @param seq    seq
     */
    public void writeUserSeq(Integer appId, String userId, String type, Long seq) {
        String key = appId + ":" + Constants.RedisConstants.SeqPrefix + ":" + userId;
        redisTemplate.opsForHash().put(key, type, seq);
    }
}
