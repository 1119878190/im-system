package com.study.im.service.group.model.resp;


import com.study.im.service.group.dao.ImGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author: lx
 * @description:
 **/
@Data
public class GetJoinedGroupResp {

    private Integer totalCount;

    private List<ImGroupEntity> groupList;

}
