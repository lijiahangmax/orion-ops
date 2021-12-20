package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.ApplicationReleaseMachineDAO;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.service.api.ApplicationReleaseMachineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 发布单机器表 服务实现类
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
@Service("applicationReleaseMachineService")
public class ApplicationReleaseMachineServiceImpl implements ApplicationReleaseMachineService {

    @Resource
    private ApplicationReleaseMachineDAO applicationReleaseMachineDAO;

    @Override
    public List<ApplicationReleaseMachineDO> getReleaseMachines(Long releaseId) {
        LambdaQueryWrapper<ApplicationReleaseMachineDO> wrapper = new LambdaQueryWrapper<ApplicationReleaseMachineDO>()
                .eq(ApplicationReleaseMachineDO::getReleaseId, releaseId);
        return applicationReleaseMachineDAO.selectList(wrapper);
    }

    @Override
    public List<ApplicationReleaseMachineDO> getReleaseMachines(List<Long> releaseIdList) {
        LambdaQueryWrapper<ApplicationReleaseMachineDO> wrapper = new LambdaQueryWrapper<ApplicationReleaseMachineDO>()
                .in(ApplicationReleaseMachineDO::getReleaseId, releaseIdList)
                .orderByAsc(ApplicationReleaseMachineDO::getId);
        return applicationReleaseMachineDAO.selectList(wrapper);
    }

}
