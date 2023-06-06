package com.study.im.service.group.model.resp;

import lombok.Data;

/**
 * @author: lx
 * @description:
 **/
@Data
public class GetRoleInGroupResp {

    private Long groupMemberId;

    private String memberId;

    /**
     * 角色  0.普通成员  1.管理员  2.群主  3.离开
      */
    private Integer role;

    private Long speakDate;

}
