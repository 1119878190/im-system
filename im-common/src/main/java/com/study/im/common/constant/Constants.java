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

    /** channel绑定的userId Key*/
    public static final String UserId = "userId";

    /** channel绑定的appId */
    public static final String AppId = "appId";

    public static final String ClientType = "clientType";

    public static final String Imei = "imei";

    /** 上次 channel 的读时间*/
    public static final String ReadTime = "readTime";


    public static final String ImCoreZkRoot = "/im-coreRoot";

    public static final String ImCoreZkRootTcp = "/tcp";

    public static final String ImCoreZkRootWeb = "/web";



    public static class RedisConstants {


        /**
         * userSign，格式：appId:userSign:
         */
        public static final String userSign = "userSign";


        /**
         * 用户上线通知channel
         */
        public static final String UserLoginChannel = "signal/channel/LOGIN_USER_INNER_QUEUE";


        /**
         * 用户session，appId + UserSessionConstants + 用户id 例如10000：userSession：lld
         */
        public static final String USER_SESSION_CONSTANT = ":userSession:";


        /**
         * 缓存客户端消息防重，格式： appId + :cacheMessage: + messageId
         */
        public static final String cacheMessage = "cacheMessage";


        public static final String OfflineMessage = "offlineMessage";


        /**
         * seq 前缀
         */
        public static final String SeqPrefix = "seq";


    }

    public static class RabbitConstants{

        public static final String Im2UserService = "pipeline2UserService";

        /**
         * Im到业务服务--单聊消息
         */
        public static final String Im2MessageService = "pipeline2MessageService";

        /**
         * Im到业务服务--群聊消息
         */
        public static final String Im2GroupService = "pipeline2GroupService";

        public static final String Im2FriendshipService = "pipeline2FriendshipService";

        /**
         * 业务服务到IM服务
         */
        public static final String MessageService2Im = "messageService2Pipeline";

        public static final String GroupService2Im = "GroupService2Pipeline";

        public static final String FriendShip2Im = "friendShip2Pipeline";

        /**
         * 单聊消息持久化
         */
        public static final String StoreP2PMessage = "storeP2PMessage";

        /**
         * 群聊消息持久化
         */
        public static final String StoreGroupMessage = "storeGroupMessage";


    }


    public static class CallbackCommand{

        public static final String ModifyUserAfter = "user.modify.after";

        public static final String CreateGroupAfter = "group.create.after";

        public static final String UpdateGroupAfter = "group.update.after";

        public static final String DestoryGroupAfter = "group.destory.after";

        public static final String TransferGroupAfter = "group.transfer.after";

        public static final String GroupMemberAddBefore = "group.member.add.before";

        public static final String GroupMemberAddAfter = "group.member.add.after";

        public static final String GroupMemberDeleteAfter = "group.member.delete.after";

        public static final String AddFriendBefore = "friend.add.before";

        public static final String AddFriendAfter = "friend.add.after";

        public static final String UpdateFriendBefore = "friend.update.before";

        public static final String UpdateFriendAfter = "friend.update.after";

        public static final String DeleteFriendAfter = "friend.delete.after";

        public static final String AddBlackAfter = "black.add.after";

        public static final String DeleteBlack = "black.delete";

        public static final String SendMessageAfter = "message.send.after";

        public static final String SendMessageBefore = "message.send.before";

    }


    public static class SeqConstants {

        public static final String Message = "messageSep";

        public static final String GroupMessage = "groupMessageSep";

        public static final String Friendship = "friendshipSeq";

//        public static final String FriendshipBlack = "friendshipBlackSeq";

        public static final String FriendshipRequest = "friendshipRequestSeq";

        public static final String FriendshipGroup = "friendshipGrouptSeq";

        public static final String Group = "groupSeq";

        public static final String Conversation = "conversationSeq";


    }

}
