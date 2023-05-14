package com.study.im.codec.pack.user;

import lombok.Data;

@Data
public class UserModifyPack {

    /**
     * 用户id
     */
    private String userId;


    /**
     * 用户名称
     */
    private String nickName;

    /**
     * 密码
     */
    private String password;

    /**
     * 照片
     */
    private String photo;

    /**
     * 性别
     */
    private String userSex;


    /**
     * 个性签名
     */
    private String selfSignature;


    /**
     * 加好友验证类型（Friend_AllowType） 1需要验证
     */
    private Integer friendAllowType;
}
