package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.ApplicationBuildDAO;
import com.orion.ops.dao.ApplicationPipelineDetailRecordDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.entity.domain.ApplicationPipelineDetailRecordDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.vo.ApplicationPipelineDetailRecordVO;
import com.orion.ops.entity.vo.ApplicationPipelineStageConfigVO;
import com.orion.ops.entity.vo.MachineInfoVO;
import com.orion.ops.service.api.ApplicationPipelineDetailRecordService;
import com.orion.ops.utils.DataQuery;
import com.orion.utils.collect.Lists;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用流水线详情明细服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:56
 */
@Service("applicationPipelineDetailRecordService")
public class ApplicationPipelineDetailRecordServiceImpl implements ApplicationPipelineDetailRecordService {

    @Resource
    private ApplicationPipelineDetailRecordDAO applicationPipelineDetailRecordDAO;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Override
    public List<ApplicationPipelineDetailRecordVO> getRecordDetails(Long recordId) {
        LambdaQueryWrapper<ApplicationPipelineDetailRecordDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailRecordDO>()
                .eq(ApplicationPipelineDetailRecordDO::getRecordId, recordId);
        // 查询列表
        List<ApplicationPipelineDetailRecordVO> details = DataQuery.of(applicationPipelineDetailRecordDAO)
                .wrapper(wrapper)
                .list(ApplicationPipelineDetailRecordVO.class);
        // 设置配置信息 构建序列
        List<ApplicationPipelineStageConfigVO> buildConfigList = details.stream()
                .map(ApplicationPipelineDetailRecordVO::getConfig)
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
                .map(ApplicationPipelineDetailRecordVO::getConfig)
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
    public List<ApplicationPipelineDetailRecordDO> selectRecordDetails(Long recordId) {
        Wrapper<ApplicationPipelineDetailRecordDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailRecordDO>()
                .eq(ApplicationPipelineDetailRecordDO::getRecordId, recordId);
        return applicationPipelineDetailRecordDAO.selectList(wrapper);
    }

    @Override
    public Integer deleteByRecordId(Long recordId) {
        Wrapper<ApplicationPipelineDetailRecordDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailRecordDO>()
                .eq(ApplicationPipelineDetailRecordDO::getRecordId, recordId);
        return applicationPipelineDetailRecordDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByRecordIdList(List<Long> recordIdList) {
        Wrapper<ApplicationPipelineDetailRecordDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailRecordDO>()
                .in(ApplicationPipelineDetailRecordDO::getRecordId, recordIdList);
        return applicationPipelineDetailRecordDAO.delete(wrapper);
    }

}
