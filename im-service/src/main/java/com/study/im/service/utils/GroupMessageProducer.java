package com.study.im.service.utils;

import com.alibaba.fastjson.JSONObject;
import com.study.im.codec.pack.group.AddGroupMemberPack;
import com.study.im.codec.pack.group.RemoveGroupMemberPack;
import com.study.im.codec.pack.group.UpdateGroupMemberPack;
import com.study.im.common.ClientType;
import com.study.im.common.enums.command.Command;
import com.study.im.common.enums.command.GroupEventCommand;
import com.study.im.common.model.ClientInfo;
import com.study.im.service.group.model.req.GroupMemberDto;
import com.study.im.service.group.service.ImGroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 群消息生成方
 *
 * @author lx
 * @date 2023/05/11
 */
@Service
public class GroupMessageProducer {

    @Autowired
    private MessageProducer messageProducer;


    @Autowired
    private ImGroupMemberService imGroupMemberService;


    /**
     * 生产消息
     *
     * @param userId     发送消息的人id
     * @param command    命令
     * @param data       数据
     * @param clientInfo 发送数据的人的client信息
     */
    public void producer(String userId, Command command, Object data, ClientInfo clientInfo) {

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(data));
        String groupId = (String) jsonObject.get("groupId");
        List<String> groupMemberId = imGroupMemberService.getGroupMemberId(groupId, clientInfo.getAppId());

        if (command.equals(GroupEventCommand.ADDED_MEMBER)) {
            //群组添加人员  发送给管理员和被加入人本身
            List<GroupMemberDto> groupManager = imGroupMemberService.getGroupManager(groupId, clientInfo.getAppId());
            AddGroupMemberPack addGroupMemberPack
                    = jsonObject.toJavaObject(AddGroupMemberPack.class);
            List<String> members = addGroupMemberPack.getMembers();
            for (GroupMemberDto groupMemberDto : groupManager) {
                if (clientInfo.getClientType() != ClientType.WEBAPI.getCode() && groupMemberDto.getMemberId().equals(userId)) {
                    messageProducer.sendToUserExceptClient(groupMemberDto.getMemberId(), command, data, clientInfo);
                } else {
                    messageProducer.sendToUserAllClient(groupMemberDto.getMemberId(), command, data, clientInfo.getAppId());
                }
            }
            for (String member : members) {
                if (clientInfo.getClientType() != ClientType.WEBAPI.getCode() && member.equals(userId)) {
                    messageProducer.sendToUserExceptClient(member, command, data, clientInfo);
                } else {
                    messageProducer.sendToUserAllClient(member, command, data, clientInfo.getAppId());
                }
            }
        } else if (command.equals(GroupEventCommand.DELETED_MEMBER)) {
            // 删除群成员
            RemoveGroupMemberPack pack = jsonObject.toJavaObject(RemoveGroupMemberPack.class);
            String member = pack.getMember();
            List<String> members = imGroupMemberService.getGroupMemberId(groupId, clientInfo.getAppId());
            members.add(member);
            for (String memberId : members) {
                if (clientInfo.getClientType() != ClientType.WEBAPI.getCode() && member.equals(userId)) {
                    messageProducer.sendToUserExceptClient(memberId, command, data, clientInfo);
                } else {
                    messageProducer.sendToUserAllClient(memberId, command, data, clientInfo.getAppId());
                }
            }
        } else if (command.equals(GroupEventCommand.UPDATED_MEMBER)) {
            // 修改群成员信息
            UpdateGroupMemberPack pack =
                    jsonObject.toJavaObject(UpdateGroupMemberPack.class);
            String memberId = pack.getMemberId();
            List<GroupMemberDto> groupManager = imGroupMemberService.getGroupManager(groupId, clientInfo.getAppId());
            GroupMemberDto groupMemberDto = new GroupMemberDto();
            groupMemberDto.setMemberId(memberId);
            groupManager.add(groupMemberDto);
            for (GroupMemberDto member : groupManager) {
                if (clientInfo.getClientType() != ClientType.WEBAPI.getCode() && member.equals(userId)) {
                    messageProducer.sendToUserExceptClient(member.getMemberId(), command, data, clientInfo);
                } else {
                    messageProducer.sendToUserAllClient(member.getMemberId(), command, data, clientInfo.getAppId());
                }
            }
        } else {
            for (String memberId : groupMemberId) {
                if (clientInfo.getClientType() != null &&
                        clientInfo.getClientType() != ClientType.WEBAPI.getCode() && memberId.equals(userId)) {
                    // 自己的消息只发送给自己其它端
                    messageProducer.sendToUserExceptClient(userId, command, data, clientInfo);

                } else {
                    // 群组内的其他人 发送所有端
                    messageProducer.sendToUserAllClient(memberId, command, data, clientInfo.getAppId());
                }

            }
        }

    }


}
