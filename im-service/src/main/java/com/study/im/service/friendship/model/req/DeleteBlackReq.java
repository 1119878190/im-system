package com.study.im.service.friendship.model.req;


import com.study.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * 取消拉黑亲求参数
 *
 * @author lx
 * @date 2023/04/29
 */
@Data
public class DeleteBlackReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String fromId;

    @NotBlank(message = "好友id不能为空")
    private String toId;

}
