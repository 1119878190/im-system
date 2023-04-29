package com.study.im.service.user.model.req;


import com.study.im.common.model.RequestBase;
import com.study.im.service.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;


@Data
public class ImportUserReq extends RequestBase {

    private List<ImUserDataEntity> userData;


}
