package com.study.im.common.route.algorithm.consistenthash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 一致性hash 抽象类
 *
 * @author lx
 * @date 2023/05/08
 */
public abstract class AbstractConsistentHash {

    protected abstract void add(long key, String value);


    protected void sort() {

    }


    protected abstract String getFirstNodeValue(String key);


    /**
     * 处理之前事件
     */
    protected abstract void processBefore();

    /**
     * 传入节点列表以及客户端信息获取一个服务节点
     *
     * @param nodes 服务节点
     * @param key   选择节点计算的hash key
     * @return
     */
    public synchronized String process(List<String> nodes, String key) {
        processBefore();
        for (String node : nodes) {
            add(hash(node), node);
        }
        sort();
        return getFirstNodeValue(key);
    }


    /**
     * hash 运算
     *
     * @param value
     * @return
     */
    public Long hash(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes = null;
        try {
            keyBytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unknown string :" + value, e);
        }

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        long truncateHashCode = hashCode & 0xffffffffL;
        return truncateHashCode;
    }


}
