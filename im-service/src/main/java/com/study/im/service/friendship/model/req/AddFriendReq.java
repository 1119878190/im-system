package com.study.im.service.friendship.model.req;


import com.study.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 添加好友请求
 *
 * @author lx
 * @date 2023/04/29
 */
@Data
public class AddFriendReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotNull(message = "toItem不能为空")
    private FriendDto toItem;

}
