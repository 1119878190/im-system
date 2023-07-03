package com.study.im.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用程序配置
 *
 * @author lx
 * @date 2023/05/07
 */
@Data
@Component
@ConfigurationProperties(prefix = "appconfig")
public class AppConfig {


    private String privateKey;


    /**
     * zookeeper连接地址
     */
    private String zkAddr;

    /**
     * zk连接超时
     */
    private Integer zkConnectTimeOut;


    /**
     * 发送信息是否校验关系链
     */
    private boolean sendMessageCheckFriend;

    /**
     * 发送消息是否校验黑名单
     */
    private boolean sendMessageCheckBlack;

    /**
     * im路由策略
     */
    private Integer imRouteWay;

    /**
     * 一致性哈希算法
     */
    private Integer consistentHashWay;

    /**
     * 回调url
     */
    private String callBackUrl;

    /**
     * 用户资料变更之后回调开关
     */
    private boolean modifyUserAfterCallback;

    /**
     * 添加好友之后回调开关
     */
    private boolean addFriendAfterCallback;

    /**
     * 添加好友之前回调开关
     */
    private boolean addFriendBeforeCallback;

    /**
     * 修改好友之后回调开关
     */
    private boolean modifyFriendAfterCallback;

    /**
     * 删除好友之后回调开关
     */
    private boolean deleteFriendAfterCallback;

    /**
     * 添加黑名单之后回调开关
     */
    private boolean addFriendShipBlackAfterCallback;

    /**
     * 删除黑名单之后回调开关
     */
    private boolean deleteFriendShipBlackAfterCallback;

    /**
     * 创建群聊之后回调开关
     */
    private boolean createGroupAfterCallback;

    /**
     * 修改群聊之后回调开关
     */
    private boolean modifyGroupAfterCallback;

    /**
     * 解散群聊之后回调开关
     */
    private boolean destroyGroupAfterCallback;

    /**
     * 删除群成员之后回调
     */
    private boolean deleteGroupMemberAfterCallback;

    /**
     * 拉人入群之前回调
     */
    private boolean addGroupMemberBeforeCallback;

    /**
     * 拉人入群之后回调
     */
    private boolean addGroupMemberAfterCallback;

    /**
     * 发送单聊消息之后
     */
    private boolean sendMessageAfterCallback;

    /**
     * 发送单聊消息之前
     */
    private boolean sendMessageBeforeCallback;


    /**
     * 是否多端同步删除会话
     */
    private Integer deleteConversationSyncMode;


    /**
     * 离线消息最大条数
     */
    private Integer offlineMessageCount;
}
