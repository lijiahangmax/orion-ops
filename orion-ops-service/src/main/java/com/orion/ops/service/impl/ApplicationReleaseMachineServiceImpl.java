package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.ApplicationReleaseMachineDAO;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.service.api.ApplicationReleaseMachineService;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Integer deleteByReleaseId(Long releaseId) {
        LambdaQueryWrapper<ApplicationReleaseMachineDO> machineWrapper = new LambdaQueryWrapper<ApplicationReleaseMachineDO>()
                .eq(ApplicationReleaseMachineDO::getReleaseId, releaseId);
        return applicationReleaseMachineDAO.delete(machineWrapper);
    }

    @Override
    public String getReleaseMachineLogPath(Long id) {
        return Optional.ofNullable(applicationReleaseMachineDAO.selectById(id))
                .map(ApplicationReleaseMachineDO::getLogPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

}
