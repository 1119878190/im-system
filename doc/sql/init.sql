
-- 用户
CREATE TABLE `im_user_data` (
                                `user_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL,
                                `app_id` int NOT NULL,
                                `nick_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '昵称',
                                `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
                                `photo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
                                `user_sex` int DEFAULT NULL,
                                `birth_day` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '生日',
                                `location` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '地址',
                                `self_signature` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '个性签名',
                                `friend_allow_type` int NOT NULL DEFAULT '1' COMMENT '加好友验证类型（Friend_AllowType） 1无需验证 2需要验证',
                                `forbidden_flag` int NOT NULL DEFAULT '0' COMMENT '禁用标识 1禁用',
                                `disable_add_friend` int NOT NULL DEFAULT '0' COMMENT '管理员禁止用户添加加好友：0 未禁用 1 已禁用',
                                `silent_flag` int NOT NULL DEFAULT '0' COMMENT '禁言标识 1禁言',
                                `user_type` int NOT NULL DEFAULT '1' COMMENT '用户类型 1普通用户 2客服 3机器人',
                                `del_flag` int NOT NULL DEFAULT '0',
                                `extra` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
                                PRIMARY KEY (`app_id`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;


-- ----------------------------
-- Table structure for im_friendship  好友关系
-- ----------------------------
DROP TABLE IF EXISTS `im_friendship`;
CREATE TABLE `im_friendship`  (
                                  `app_id` int(20) NOT NULL COMMENT 'app_id',
                                  `from_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'from_id',
                                  `to_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'to_id',
                                  `remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                  `status` int(10) NULL DEFAULT NULL COMMENT '状态 1正常 2删除',
                                  `black` int(10) NULL DEFAULT NULL COMMENT '1正常 2拉黑',
                                  `create_time` bigint(20) NULL DEFAULT NULL,
                                  `friend_sequence` bigint(20) NULL DEFAULT NULL,
                                  `black_sequence` bigint(20) NULL DEFAULT NULL,
                                  `add_source` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源',
                                  `extra` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源',
                                  PRIMARY KEY (`app_id`, `from_id`, `to_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;



-- ----------------------------
-- Table structure for im_friendship_request  好友请求
-- ----------------------------
DROP TABLE IF EXISTS `im_friendship_request`;
CREATE TABLE `im_friendship_request`  (
                                          `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                          `app_id` int(20) NULL DEFAULT NULL COMMENT 'app_id',
                                          `from_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'from_id',
                                          `to_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'to_id',
                                          `remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                          `read_status` int(10) NULL DEFAULT NULL COMMENT '是否已读 1已读',
                                          `add_source` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '好友来源',
                                          `add_wording` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '好友验证信息',
                                          `approve_status` int(10) NULL DEFAULT NULL COMMENT '审批状态 1同意 2拒绝',
                                          `create_time` bigint(20) NULL DEFAULT NULL,
                                          `update_time` bigint(20) NULL DEFAULT NULL,
                                          `sequence` bigint(20) NULL DEFAULT NULL,
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
