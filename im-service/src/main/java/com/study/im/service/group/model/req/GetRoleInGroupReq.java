package com.study.im.service.group.model.req;


import com.study.im.common.model.RequestBase;
import lombok.Data;

import java.util.List;

/**
 * @author: lx
 * @description:
 **/
@Data
public class GetRoleInGroupReq extends RequestBase {

    private String groupId;

    private List<String> memberId;
}
