package com.study.im.service.friendship.model.req;

import com.study.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 获取所有好友请求
 *
 * @author lx
 * @date 2023/04/29
 */
@Data
public class GetAllFriendShipReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String fromId;
}
