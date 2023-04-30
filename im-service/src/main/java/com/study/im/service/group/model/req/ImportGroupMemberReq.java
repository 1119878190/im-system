package com.study.im.service.group.model.req;


import com.study.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @description:
 * @author: lx
 * @version: 1.0
 */
@Data
public class ImportGroupMemberReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    private List<GroupMemberDto> members;

}
