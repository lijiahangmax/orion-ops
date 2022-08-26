package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.AlarmGroupUserDAO;
import com.orion.ops.entity.domain.AlarmGroupUserDO;
import com.orion.ops.service.api.AlarmGroupUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 报警组员服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 10:29
 */
@Service("alarmGroupUserService")
public class AlarmGroupUserServiceImpl implements AlarmGroupUserService {

    @Resource
    private AlarmGroupUserDAO alarmGroupUserDAO;

    @Override
    public List<AlarmGroupUserDO> selectByGroupId(Long groupId) {
        LambdaQueryWrapper<AlarmGroupUserDO> wrapper = new LambdaQueryWrapper<AlarmGroupUserDO>()
                .eq(AlarmGroupUserDO::getGroupId, groupId);
        return alarmGroupUserDAO.selectList(wrapper);
    }

    @Override
    public List<AlarmGroupUserDO> selectByGroupIdList(List<Long> groupIdList) {
        LambdaQueryWrapper<AlarmGroupUserDO> wrapper = new LambdaQueryWrapper<AlarmGroupUserDO>()
                .in(AlarmGroupUserDO::getGroupId, groupIdList);
        return alarmGroupUserDAO.selectList(wrapper);
    }

    @Override
    public Integer deleteByGroupId(Long groupId) {
        LambdaQueryWrapper<AlarmGroupUserDO> wrapper = new LambdaQueryWrapper<AlarmGroupUserDO>()
                .eq(AlarmGroupUserDO::getGroupId, groupId);
        return alarmGroupUserDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByUserId(Long userId) {
        LambdaQueryWrapper<AlarmGroupUserDO> wrapper = new LambdaQueryWrapper<AlarmGroupUserDO>()
                .eq(AlarmGroupUserDO::getUserId, userId);
        return alarmGroupUserDAO.delete(wrapper);
    }

}
