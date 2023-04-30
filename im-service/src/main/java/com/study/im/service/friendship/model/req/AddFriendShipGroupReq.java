package com.study.im.service.friendship.model.req;


import com.study.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


/**
 * 添加好友分组
 *
 * @author lx
 * @date 2023/04/30
 */
@Data
public class AddFriendShipGroupReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    public String fromId;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    private List<String> toIds;

}
