package com.study.im.service.friendship.model.callback;

import com.study.im.service.friendship.model.req.FriendDto;
import lombok.Data;

/**
 * 添加朋友之后回调dto
 *
 * @author lx
 * @date 2023/05/09
 */
@Data
public class AddFriendAfterCallbackDto {

    private String fromId;

    private FriendDto toItem;

}
