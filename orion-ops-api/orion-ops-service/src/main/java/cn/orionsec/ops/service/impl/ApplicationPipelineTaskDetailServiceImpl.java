/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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

import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.ops.dao.ApplicationBuildDAO;
import cn.orionsec.ops.dao.ApplicationPipelineTaskDetailDAO;
import cn.orionsec.ops.dao.MachineInfoDAO;
import cn.orionsec.ops.entity.domain.ApplicationBuildDO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineStageConfigVO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineTaskDetailVO;
import cn.orionsec.ops.entity.vo.machine.MachineInfoVO;
import cn.orionsec.ops.service.api.ApplicationPipelineTaskDetailService;
import cn.orionsec.ops.utils.DataQuery;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用流水线任务详情服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:56
 */
@Service("applicationPipelineTaskDetailService")
public class ApplicationPipelineTaskDetailServiceImpl implements ApplicationPipelineTaskDetailService {

    @Resource
    private ApplicationPipelineTaskDetailDAO applicationPipelineTaskDetailDAO;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Override
    public List<ApplicationPipelineTaskDetailVO> getTaskDetails(Long taskId) {
        LambdaQueryWrapper<ApplicationPipelineTaskDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskDetailDO>()
                .eq(ApplicationPipelineTaskDetailDO::getTaskId, taskId);
        // 查询列表
        List<ApplicationPipelineTaskDetailVO> details = DataQuery.of(applicationPipelineTaskDetailDAO)
                .wrapper(wrapper)
                .list(ApplicationPipelineTaskDetailVO.class);
        // 设置配置信息 构建序列
        List<ApplicationPipelineStageConfigVO> buildConfigList = details.stream()
                .map(ApplicationPipelineTaskDetailVO::getConfig)
                .filter(s -> s.getBuildId() != null)
                .collect(Collectors.toList());
        if (!buildConfigList.isEmpty()) {
            // 构建版本id
            List<Long> buildIdList = buildConfigList.stream()
                    .map(ApplicationPipelineStageConfigVO::getBuildId)
                    .collect(Collectors.toList());
            Map<Long, ApplicationBuildDO> buildMap = applicationBuildDAO.selectBatchIds(buildIdList).stream()
                    .collect(Collectors.toMap(ApplicationBuildDO::getId, Function.identity()));
            // 设置构建序列
            for (ApplicationPipelineStageConfigVO buildConfig : buildConfigList) {
                Optional.ofNullable(buildConfig.getBuildId())
                        .map(buildMap::get)
                        .map(ApplicationBuildDO::getBuildSeq)
                        .ifPresent(buildConfig::setBuildSeq);
            }
        }
        // 设置配置信息 构建序列
        List<ApplicationPipelineStageConfigVO> specifyMachineReleaseConfigList = details.stream()
                .map(ApplicationPipelineTaskDetailVO::getConfig)
                .filter(s -> Lists.isNotEmpty(s.getMachineIdList()))
                .collect(Collectors.toList());
        if (!specifyMachineReleaseConfigList.isEmpty()) {
            List<Long> machineIdList = specifyMachineReleaseConfigList.stream()
                    .map(ApplicationPipelineStageConfigVO::getMachineIdList)
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, MachineInfoDO> machineMap = machineInfoDAO.selectBatchIds(machineIdList).stream()
                    .collect(Collectors.toMap(MachineInfoDO::getId, Function.identity()));
            // 设置机器信息
            for (ApplicationPipelineStageConfigVO config : specifyMachineReleaseConfigList) {
                List<MachineInfoVO> machineList = config.getMachineIdList().stream()
                        .map(machineMap::get)
                        .filter(Objects::nonNull)
                        .map(s -> Converts.to(s, MachineInfoVO.class))
                        .collect(Collectors.toList());
                config.setMachineList(machineList);
            }
        }
        return details;
    }

    @Override
    public List<ApplicationPipelineTaskDetailDO> selectTaskDetails(Long taskId) {
        Wrapper<ApplicationPipelineTaskDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskDetailDO>()
                .eq(ApplicationPipelineTaskDetailDO::getTaskId, taskId);
        return applicationPipelineTaskDetailDAO.selectList(wrapper);
    }

    @Override
    public List<ApplicationPipelineTaskDetailDO> selectTaskDetails(List<Long> taskIdList) {
        Wrapper<ApplicationPipelineTaskDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskDetailDO>()
                .in(ApplicationPipelineTaskDetailDO::getTaskId, taskIdList);
        return applicationPipelineTaskDetailDAO.selectList(wrapper);
    }

    @Override
    public Integer deleteByTaskId(Long taskId) {
        Wrapper<ApplicationPipelineTaskDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskDetailDO>()
                .eq(ApplicationPipelineTaskDetailDO::getTaskId, taskId);
        return applicationPipelineTaskDetailDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByTaskIdList(List<Long> taskIdList) {
        Wrapper<ApplicationPipelineTaskDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskDetailDO>()
                .in(ApplicationPipelineTaskDetailDO::getTaskId, taskIdList);
        return applicationPipelineTaskDetailDAO.delete(wrapper);
    }

}
