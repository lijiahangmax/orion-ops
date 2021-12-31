package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.ApplicationReleaseActionDAO;
import com.orion.ops.entity.domain.ApplicationReleaseActionDO;
import com.orion.ops.service.api.ApplicationReleaseActionService;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 发布单操作步骤 服务实现类
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
@Service("applicationReleaseActionService")
public class ApplicationReleaseActionServiceImpl implements ApplicationReleaseActionService {

    @Resource
    private ApplicationReleaseActionDAO applicationReleaseActionDAO;

    @Override
    public List<ApplicationReleaseActionDO> getReleaseActionByReleaseMachineId(Long releaseMachineId) {
        Wrapper<ApplicationReleaseActionDO> wrapper = new LambdaQueryWrapper<ApplicationReleaseActionDO>()
                .eq(ApplicationReleaseActionDO::getReleaseMachineId, releaseMachineId)
                .orderByAsc(ApplicationReleaseActionDO::getId);
        return applicationReleaseActionDAO.selectList(wrapper);
    }

    @Override
    public String getReleaseActionLogPath(Long id) {
        return Optional.ofNullable(applicationReleaseActionDAO.selectById(id))
                .map(ApplicationReleaseActionDO::getLogPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

}
