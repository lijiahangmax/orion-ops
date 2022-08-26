package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.alarm.AlarmGroupNotifyType;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.request.alarm.AlarmGroupRequest;
import com.orion.ops.entity.vo.alarm.AlarmGroupVO;
import com.orion.ops.service.api.AlarmGroupNotifyService;
import com.orion.ops.service.api.AlarmGroupService;
import com.orion.ops.service.api.AlarmGroupUserService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报警组服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/25 15:39
 */
@Service("alarmGroupService")
public class AlarmGroupServiceImpl implements AlarmGroupService {

    @Resource
    private AlarmGroupDAO alarmGroupDAO;

    @Resource
    private AlarmGroupUserDAO alarmGroupUserDAO;

    @Resource
    private AlarmGroupNotifyDAO alarmGroupNotifyDAO;

    @Resource
    private UserInfoDAO userInfoDAO;

    @Resource
    private WebhookConfigDAO webhookConfigDAO;

    @Resource
    private AlarmGroupUserService alarmGroupUserService;

    @Resource
    private AlarmGroupNotifyService alarmGroupNotifyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAlarmGroup(AlarmGroupRequest request) {
        String name = request.getName();
        List<Long> userIdList = request.getUserIdList();
        List<Long> webhookIdList = request.getNotifyIdList();
        // 检查名称是否存在
        this.checkNamePresent(null, name);
        // 查询组员
        List<UserInfoDO> users = userInfoDAO.selectBatchIds(userIdList);
        Valid.eq(users.size(), userIdList.size(), MessageConst.UNKNOWN_USER);
        // 查询通知方式
        List<WebhookConfigDO> webhooks = webhookConfigDAO.selectBatchIds(webhookIdList);
        Valid.eq(webhooks.size(), webhookIdList.size(), MessageConst.WEBHOOK_ABSENT);
        // 插入组表
        AlarmGroupDO group = new AlarmGroupDO();
        group.setGroupName(name);
        group.setGroupDescription(request.getDescription());
        alarmGroupDAO.insert(group);
        Long groupId = group.getId();
        // 插入组员表
        users.stream().map(u -> {
            AlarmGroupUserDO groupUser = new AlarmGroupUserDO();
            groupUser.setGroupId(groupId);
            groupUser.setUserId(u.getId());
            groupUser.setUsername(u.getUsername());
            return groupUser;
        }).forEach(alarmGroupUserDAO::insert);
        // 插入通知方式
        webhookIdList.stream().map(wid -> {
            AlarmGroupNotifyDO groupNotify = new AlarmGroupNotifyDO();
            groupNotify.setGroupId(groupId);
            groupNotify.setNotifyId(wid);
            groupNotify.setNotifyType(AlarmGroupNotifyType.WEBHOOK.getType());
            return groupNotify;
        }).forEach(alarmGroupNotifyDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        return groupId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateAlarmGroup(AlarmGroupRequest request) {
        Long id = request.getId();
        String name = request.getName();
        List<Long> userIdList = request.getUserIdList();
        List<Long> webhookIdList = request.getNotifyIdList();
        // 查询
        AlarmGroupDO group = alarmGroupDAO.selectById(id);
        Valid.notNull(group, MessageConst.ALARM_GROUP_ABSENT);
        // 检查名称是否存在
        this.checkNamePresent(id, name);
        // 查询组员
        List<UserInfoDO> users = userInfoDAO.selectBatchIds(userIdList);
        Valid.eq(users.size(), userIdList.size(), MessageConst.UNKNOWN_USER);
        // 查询通知方式
        List<WebhookConfigDO> webhooks = webhookConfigDAO.selectBatchIds(webhookIdList);
        Valid.eq(webhooks.size(), webhookIdList.size(), MessageConst.WEBHOOK_ABSENT);
        // 修改组表
        AlarmGroupDO update = new AlarmGroupDO();
        update.setId(id);
        update.setGroupName(name);
        update.setGroupDescription(request.getDescription());
        int effect = alarmGroupDAO.updateById(update);
        // 重新插入组员表
        effect += alarmGroupUserService.deleteByGroupId(id);
        users.stream().map(u -> {
            AlarmGroupUserDO groupUser = new AlarmGroupUserDO();
            groupUser.setGroupId(id);
            groupUser.setUserId(u.getId());
            groupUser.setUsername(u.getUsername());
            return groupUser;
        }).forEach(alarmGroupUserDAO::insert);
        // 重新插入通知方式
        effect += alarmGroupNotifyService.deleteByGroupId(id);
        webhookIdList.stream().map(wid -> {
            AlarmGroupNotifyDO groupNotify = new AlarmGroupNotifyDO();
            groupNotify.setGroupId(id);
            groupNotify.setNotifyId(wid);
            groupNotify.setNotifyType(AlarmGroupNotifyType.WEBHOOK.getType());
            return groupNotify;
        }).forEach(alarmGroupNotifyDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.BEFORE, group.getGroupName());
        EventParamsHolder.addParams(request);
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteAlarmGroup(Long id) {
        // 查询
        AlarmGroupDO group = alarmGroupDAO.selectById(id);
        Valid.notNull(group, MessageConst.ALARM_GROUP_ABSENT);
        // 删除组
        int effect = alarmGroupDAO.deleteById(id);
        // 删除组员
        effect += alarmGroupUserService.deleteByGroupId(id);
        // 删除通知方式
        effect += alarmGroupNotifyService.deleteByGroupId(id);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, group.getGroupName());
        return effect;
    }

    @Override
    public DataGrid<AlarmGroupVO> getAlarmGroupList(AlarmGroupRequest request) {
        LambdaQueryWrapper<AlarmGroupDO> wrapper = new LambdaQueryWrapper<AlarmGroupDO>()
                .like(Strings.isNotBlank(request.getName()), AlarmGroupDO::getGroupName, request.getName())
                .like(Strings.isNotBlank(request.getDescription()), AlarmGroupDO::getGroupDescription, request.getDescription());
        // 查询列表
        DataGrid<AlarmGroupVO> dataGrid = DataQuery.of(alarmGroupDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(AlarmGroupVO.class);
        if (dataGrid.isEmpty()) {
            return dataGrid;
        }
        List<Long> groupIdList = dataGrid.stream()
                .map(AlarmGroupVO::getId)
                .collect(Collectors.toList());
        // 查询组员信息
        List<AlarmGroupUserDO> groupUsers = alarmGroupUserService.selectByGroupIdList(groupIdList);
        if (groupUsers.isEmpty()) {
            return dataGrid;
        }
        // 查询用户信息
        List<Long> userIdList = groupUsers.stream()
                .map(AlarmGroupUserDO::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> userNickNameMap = userInfoDAO.selectBatchIds(userIdList).stream()
                .collect(Collectors.toMap(UserInfoDO::getId, UserInfoDO::getNickname));
        // 组合用户信息
        dataGrid.forEach(row -> {
            String groupUserName = groupUsers.stream()
                    .filter(s -> s.getGroupId().equals(row.getId()))
                    .map(AlarmGroupUserDO::getUserId)
                    .map(userNickNameMap::get)
                    .collect(Collectors.joining(", "));
            row.setGroupUsers(groupUserName);
        });
        return dataGrid;
    }

    @Override
    public AlarmGroupVO getAlarmGroupDetail(Long id) {
        // 查询组
        AlarmGroupDO alarmGroup = alarmGroupDAO.selectById(id);
        Valid.notNull(alarmGroup, MessageConst.ALARM_GROUP_ABSENT);
        AlarmGroupVO group = Converts.to(alarmGroup, AlarmGroupVO.class);
        // 查询组员
        List<AlarmGroupUserDO> groupUsers = alarmGroupUserService.selectByGroupId(id);
        List<Long> userIdList = groupUsers.stream()
                .map(AlarmGroupUserDO::getUserId)
                .collect(Collectors.toList());
        group.setUserIdList(userIdList);
        // 查询通知方式
        List<AlarmGroupNotifyDO> groupNotifies = alarmGroupNotifyService.selectByGroupId(id);
        List<Long> notifyIdList = groupNotifies.stream()
                .map(AlarmGroupNotifyDO::getNotifyId)
                .collect(Collectors.toList());
        group.setNotifyIdList(notifyIdList);
        return group;
    }

    /**
     * 检查是否存在
     *
     * @param id   id
     * @param name name
     */
    private void checkNamePresent(Long id, String name) {
        LambdaQueryWrapper<AlarmGroupDO> presentWrapper = new LambdaQueryWrapper<AlarmGroupDO>()
                .ne(id != null, AlarmGroupDO::getId, id)
                .eq(AlarmGroupDO::getGroupName, name);
        boolean present = DataQuery.of(alarmGroupDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

}
