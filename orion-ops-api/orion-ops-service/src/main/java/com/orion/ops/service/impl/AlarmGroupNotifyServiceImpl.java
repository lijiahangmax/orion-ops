package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.constant.alarm.AlarmGroupNotifyType;
import com.orion.ops.dao.AlarmGroupNotifyDAO;
import com.orion.ops.entity.domain.AlarmGroupNotifyDO;
import com.orion.ops.service.api.AlarmGroupNotifyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 报警组通知方式服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 10:29
 */
@Service("alarmGroupNotifyService")
public class AlarmGroupNotifyServiceImpl implements AlarmGroupNotifyService {

    @Resource
    private AlarmGroupNotifyDAO alarmGroupNotifyDAO;

    @Override
    public List<AlarmGroupNotifyDO> selectByGroupId(Long groupId) {
        LambdaQueryWrapper<AlarmGroupNotifyDO> wrapper = new LambdaQueryWrapper<AlarmGroupNotifyDO>()
                .eq(AlarmGroupNotifyDO::getGroupId, groupId);
        return alarmGroupNotifyDAO.selectList(wrapper);
    }

    @Override
    public List<AlarmGroupNotifyDO> selectByGroupIdList(List<Long> groupIdList) {
        LambdaQueryWrapper<AlarmGroupNotifyDO> wrapper = new LambdaQueryWrapper<AlarmGroupNotifyDO>()
                .in(AlarmGroupNotifyDO::getGroupId, groupIdList);
        return alarmGroupNotifyDAO.selectList(wrapper);
    }

    @Override
    public Integer deleteByGroupId(Long groupId) {
        LambdaQueryWrapper<AlarmGroupNotifyDO> wrapper = new LambdaQueryWrapper<AlarmGroupNotifyDO>()
                .eq(AlarmGroupNotifyDO::getGroupId, groupId);
        return alarmGroupNotifyDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByWebhookId(Long webhookId) {
        LambdaQueryWrapper<AlarmGroupNotifyDO> wrapper = new LambdaQueryWrapper<AlarmGroupNotifyDO>()
                .eq(AlarmGroupNotifyDO::getNotifyId, webhookId)
                .eq(AlarmGroupNotifyDO::getNotifyType, AlarmGroupNotifyType.WEBHOOK.getType());
        return alarmGroupNotifyDAO.delete(wrapper);
    }

}
