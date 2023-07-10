package com.study.im.common.model;

import lombok.Data;

import java.util.List;

/**
 * @author: liux
 * @description: 数据增量同步接口返回
 **/
@Data
public class SyncResp<T> {

    /**
     * 需要同步数据的最大seq
     */
    private Long maxSequence;

    /**
     * 是否增量同步完成
     */
    private boolean isCompleted;

    private List<T> dataList;

}
