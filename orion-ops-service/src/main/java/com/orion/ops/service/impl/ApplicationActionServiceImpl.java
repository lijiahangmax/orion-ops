package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.dao.ApplicationActionDAO;
import com.orion.ops.entity.domain.ApplicationActionDO;
import com.orion.ops.entity.request.ApplicationConfigActionRequest;
import com.orion.ops.entity.request.ApplicationConfigRequest;
import com.orion.ops.service.api.ApplicationActionService;
import com.orion.utils.Exceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * app发布步骤实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/8 18:30
 */
@Service("applicationActionService")
public class ApplicationActionServiceImpl implements ApplicationActionService {

    @Resource
    private ApplicationActionDAO applicationActionDAO;

    @Override
    public Integer deleteAppActionByAppProfileId(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationActionDO> wrapper = new LambdaQueryWrapper<ApplicationActionDO>()
                .eq(appId != null, ApplicationActionDO::getAppId, appId)
                .eq(profileId != null, ApplicationActionDO::getProfileId, profileId);
        return applicationActionDAO.delete(wrapper);
    }

    @Override
    public List<ApplicationActionDO> getAppProfileActions(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationActionDO> wrapper = new LambdaQueryWrapper<ApplicationActionDO>()
                .eq(ApplicationActionDO::getAppId, appId)
                .eq(ApplicationActionDO::getProfileId, profileId);
        return applicationActionDAO.selectList(wrapper);
    }

    @Override
    public Integer getAppProfileActionCount(Long appId, Long profileId, Integer stageType) {
        LambdaQueryWrapper<ApplicationActionDO> wrapper = new LambdaQueryWrapper<ApplicationActionDO>()
                .eq(ApplicationActionDO::getAppId, appId)
                .eq(ApplicationActionDO::getProfileId, profileId)
                .eq(stageType != null, ApplicationActionDO::getStageType, stageType);
        return applicationActionDAO.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configAppAction(ApplicationConfigRequest request) {
        Long appId = request.getAppId();
        Long profileId = request.getProfileId();
        StageType stageType = StageType.of(request.getStageType());
        // 删除
        LambdaQueryWrapper<ApplicationActionDO> deleteWrapper = new LambdaQueryWrapper<ApplicationActionDO>()
                .eq(ApplicationActionDO::getAppId, appId)
                .eq(ApplicationActionDO::getProfileId, profileId)
                .eq(ApplicationActionDO::getStageType, stageType.getType());
        applicationActionDAO.delete(deleteWrapper);
        // 插入
        List<ApplicationConfigActionRequest> actions;
        if (StageType.BUILD.equals(stageType)) {
            actions = request.getBuildActions();
        } else if (StageType.RELEASE.equals(stageType)) {
            actions = request.getReleaseActions();
        } else {
            throw Exceptions.unsupported();
        }
        for (ApplicationConfigActionRequest action : actions) {
            ApplicationActionDO insert = new ApplicationActionDO();
            insert.setAppId(appId);
            insert.setProfileId(profileId);
            insert.setActionName(action.getName());
            insert.setActionType(action.getType());
            insert.setStageType(stageType.getType());
            insert.setActionCommand(action.getCommand());
            applicationActionDAO.insert(insert);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAppProfileAction(Long appId, Long sourceProfileId, Long targetProfileId) {
        // 删除
        LambdaQueryWrapper<ApplicationActionDO> deleteWrapper = new LambdaQueryWrapper<ApplicationActionDO>()
                .eq(ApplicationActionDO::getAppId, appId)
                .eq(ApplicationActionDO::getProfileId, targetProfileId);
        applicationActionDAO.delete(deleteWrapper);
        // 查询
        LambdaQueryWrapper<ApplicationActionDO> queryWrapper = new LambdaQueryWrapper<ApplicationActionDO>()
                .eq(ApplicationActionDO::getAppId, appId)
                .eq(ApplicationActionDO::getProfileId, sourceProfileId);
        List<ApplicationActionDO> actions = applicationActionDAO.selectList(queryWrapper);
        // 新增
        for (ApplicationActionDO action : actions) {
            action.setId(null);
            action.setCreateTime(null);
            action.setUpdateTime(null);
            action.setProfileId(targetProfileId);
            applicationActionDAO.insert(action);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyAppAction(Long appId, Long targetAppId) {
        // 查询
        LambdaQueryWrapper<ApplicationActionDO> queryWrapper = new LambdaQueryWrapper<ApplicationActionDO>()
                .eq(ApplicationActionDO::getAppId, appId);
        List<ApplicationActionDO> actions = applicationActionDAO.selectList(queryWrapper);
        // 插入
        for (ApplicationActionDO action : actions) {
            action.setId(null);
            action.setAppId(targetAppId);
            action.setCreateTime(null);
            action.setUpdateTime(null);
            applicationActionDAO.insert(action);
        }
    }

}
