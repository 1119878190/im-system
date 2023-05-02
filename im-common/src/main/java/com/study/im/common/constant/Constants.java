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

    /** 上次 channel 的读时间*/
    public static final String ReadTime = "readTime";


    public static final String ImCoreZkRoot = "/im-coreRoot";

    public static final String ImCoreZkRootTcp = "/tcp";

    public static final String ImCoreZkRootWeb = "/web";



    public static class RedisConstants {

        // 用户Session    格式： appid + USER_SESSION_CONSTANT + 用户id
        public static final String USER_SESSION_CONSTANT = ":userSession:";

    }

    public static class RabbitConstants{

        public static final String Im2UserService = "pipeline2UserService";

        public static final String Im2MessageService = "pipeline2MessageService";

        public static final String Im2GroupService = "pipeline2GroupService";

        public static final String Im2FriendshipService = "pipeline2FriendshipService";

        public static final String MessageService2Im = "messageService2Pipeline";

        public static final String GroupService2Im = "GroupService2Pipeline";

        public static final String FriendShip2Im = "friendShip2Pipeline";

        public static final String StoreP2PMessage = "storeP2PMessage";

        public static final String StoreGroupMessage = "storeGroupMessage";


    }

}
