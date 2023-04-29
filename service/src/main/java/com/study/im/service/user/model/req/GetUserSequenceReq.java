package com.study.im.service.user.model.req;


import com.study.im.common.model.RequestBase;
import lombok.Data;

/**
 * @description:
 * @author: lx
 * @version: 1.0
 */
@Data
public class GetUserSequenceReq extends RequestBase {

    private String userId;

}
