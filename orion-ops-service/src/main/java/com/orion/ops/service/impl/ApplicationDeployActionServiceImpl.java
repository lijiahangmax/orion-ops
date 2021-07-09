package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.ApplicationDeployActionDAO;
import com.orion.ops.entity.domain.ApplicationDeployActionDO;
import com.orion.ops.entity.vo.ApplicationDeployActionVO;
import com.orion.ops.service.api.ApplicationDeployActionService;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * app部署步骤实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/8 18:30
 */
@Service("applicationDeployActionService")
public class ApplicationDeployActionServiceImpl implements ApplicationDeployActionService {

    @Resource
    private ApplicationDeployActionDAO applicationDeployActionDAO;

    @Override
    public Integer deleteAppActionByAppProfileId(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationDeployActionDO> wrapper = new LambdaQueryWrapper<ApplicationDeployActionDO>()
                .eq(appId != null, ApplicationDeployActionDO::getAppId, appId)
                .eq(profileId != null, ApplicationDeployActionDO::getProfileId, profileId);
        return applicationDeployActionDAO.delete(wrapper);
    }

    @Override
    public List<ApplicationDeployActionVO> getDeployActions(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationDeployActionDO> wrapper = new LambdaQueryWrapper<ApplicationDeployActionDO>()
                .eq(ApplicationDeployActionDO::getAppId, appId)
                .eq(ApplicationDeployActionDO::getProfileId, profileId);
        List<ApplicationDeployActionVO> actions = applicationDeployActionDAO.selectList(wrapper).stream()
                .map(s -> Converts.to(s, ApplicationDeployActionVO.class))
                .collect(Collectors.toList());
        for (int i = 0; i < actions.size(); i++) {
            actions.get(0).setStep(i + 1);
        }
        return actions;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAppProfileAction(Long appId, Long profileId, Long syncProfileId) {
        // 删除
        LambdaQueryWrapper<ApplicationDeployActionDO> deleteWrapper = new LambdaQueryWrapper<ApplicationDeployActionDO>()
                .eq(ApplicationDeployActionDO::getAppId, appId)
                .eq(ApplicationDeployActionDO::getProfileId, syncProfileId);
        applicationDeployActionDAO.delete(deleteWrapper);
        // 查询
        LambdaQueryWrapper<ApplicationDeployActionDO> queryWrapper = new LambdaQueryWrapper<ApplicationDeployActionDO>()
                .eq(ApplicationDeployActionDO::getAppId, appId)
                .eq(ApplicationDeployActionDO::getProfileId, profileId);
        List<ApplicationDeployActionDO> actions = applicationDeployActionDAO.selectList(queryWrapper);
        // 新增
        for (ApplicationDeployActionDO action : actions) {
            action.setId(null);
            action.setCreateTime(null);
            action.setUpdateTime(null);
            action.setProfileId(syncProfileId);
            applicationDeployActionDAO.insert(action);
        }
    }

}
