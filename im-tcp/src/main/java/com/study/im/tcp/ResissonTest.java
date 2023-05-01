package com.study.im.tcp;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

/**
 * resisson测试
 *
 * @author lx
 * @date 2023/05/01
 */
public class ResissonTest {

    public static void main(String[] args) {


        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.218.129:6379");
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        RedissonClient redissonClient = Redisson.create(config);


        RBucket<Object> im = redissonClient.getBucket("im");
        System.out.println(im.get());
        im.set("hello");
        System.out.println(im.get());


        RMap<Object, Object> map = redissonClient.getMap("imMap");
        System.out.println(map.get("web"));
        map.put("web","student1");
        System.out.println(map.get("web"));


        // 发布订阅
        RTopic topic = redissonClient.getTopic("topic");
        topic.addListener(String.class, (charSequence, s) -> System.out.println("收到了消息"+ s));
        topic.publish("hello topic");

    }
}
