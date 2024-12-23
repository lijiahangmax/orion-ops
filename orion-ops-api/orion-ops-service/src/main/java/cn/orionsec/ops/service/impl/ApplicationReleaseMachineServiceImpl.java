/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.service.impl;

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.dao.ApplicationReleaseMachineDAO;
import cn.orionsec.ops.entity.domain.ApplicationReleaseMachineDO;
import cn.orionsec.ops.service.api.ApplicationReleaseMachineService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 发布任务机器表 服务实现类
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
    public List<Long> getReleaseMachineIdList(List<Long> releaseIdList) {
        LambdaQueryWrapper<ApplicationReleaseMachineDO> wrapper = new LambdaQueryWrapper<ApplicationReleaseMachineDO>()
                .select(ApplicationReleaseMachineDO::getId)
                .in(ApplicationReleaseMachineDO::getReleaseId, releaseIdList)
                .orderByAsc(ApplicationReleaseMachineDO::getId);
        return applicationReleaseMachineDAO.selectList(wrapper).stream()
                .map(ApplicationReleaseMachineDO::getId)
                .collect(Collectors.toList());
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
