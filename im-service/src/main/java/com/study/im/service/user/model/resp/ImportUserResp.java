package com.study.im.service.user.model.resp;

import lombok.Data;

import java.util.List;

/**
 * @author: lx
 * @description:
 **/
@Data
public class ImportUserResp {

    private List<String> successId;

    private List<String> errorId;

}
