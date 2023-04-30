package com.study.im.service.friendship.model.req;


import com.study.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * 获取好友关系
 *
 * @author lx
 * @date 2023/04/29
 */
@Data
public class GetRelationReq extends RequestBase {
    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotBlank(message = "toId不能为空")
    private String toId;
}
