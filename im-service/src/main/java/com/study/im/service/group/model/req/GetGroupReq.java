package com.study.im.service.group.model.req;


import com.study.im.common.model.RequestBase;
import lombok.Data;

/**
 * @author: lx
 * @description:
 **/
@Data
public class GetGroupReq extends RequestBase {

    private String groupId;

}
