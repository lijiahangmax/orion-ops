package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.dao.ApplicationMachineDAO;
import com.orion.ops.entity.domain.ApplicationMachineDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.ApplicationConfigRequest;
import com.orion.ops.entity.vo.ApplicationMachineVO;
import com.orion.ops.service.api.ApplicationMachineService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.service.api.ReleaseInfoService;
import com.orion.ops.utils.Valid;
import com.orion.spring.SpringHolder;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 应用机器 service 实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/9 18:24
 */
@Service("applicationMachineService")
public class ApplicationMachineServiceImpl implements ApplicationMachineService {

    @Resource
    private ApplicationMachineDAO applicationMachineDAO;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private ReleaseInfoService releaseInfoService;

    @Override
    public Long getAppProfileMachineId(Long machineId, Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getMachineId, machineId)
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId);
        return Optional.ofNullable(applicationMachineDAO.selectOne(wrapper))
                .map(ApplicationMachineDO::getMachineId)
                .orElse(null);
    }

    @Override
    public List<Long> getAppProfileMachineIdList(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getMachineId, profileId);
        return applicationMachineDAO.selectList(wrapper).stream()
                .map(ApplicationMachineDO::getMachineId)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getAppProfileMachineCount(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getMachineId, profileId);
        return applicationMachineDAO.selectCount(wrapper);
    }

    @Override
    public List<ApplicationMachineVO> getAppProfileMachineList(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId);
        return applicationMachineDAO.selectList(wrapper)
                .stream()
                .map(m -> {
                    // 查询机器
                    MachineInfoDO machine = machineInfoService.selectById(m.getMachineId());
                    ApplicationMachineVO machineVO = Converts.to(machine, ApplicationMachineVO.class);
                    machineVO.setId(m.getId());
                    // 查询版本
                    Optional.ofNullable(m.getReleaseId())
                            .map(releaseInfoService::getReleaseBill)
                            .ifPresent(r -> {
                                machineVO.setReleaseId(r.getId());
                                machineVO.setBranchName(r.getBranchName());
                                machineVO.setCommitId(r.getCommitId());
                            });
                    return machineVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Integer deleteAppMachineByMachineId(Long machineId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getMachineId, machineId);
        return applicationMachineDAO.delete(wrapper);
    }

    @Override
    public Integer deleteAppMachineByAppProfileId(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(appId != null, ApplicationMachineDO::getAppId, appId)
                .eq(profileId != null, ApplicationMachineDO::getProfileId, profileId);
        return applicationMachineDAO.delete(wrapper);
    }

    @Override
    public Integer deleteAppMachineByAppProfileMachineId(Long appId, Long profileId, Long machineId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId)
                .eq(ApplicationMachineDO::getMachineId, machineId);
        return applicationMachineDAO.delete(wrapper);
    }

    @Override
    public List<Long> selectAppProfileMachineIdList(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId);
        return applicationMachineDAO.selectList(wrapper)
                .stream()
                .map(ApplicationMachineDO::getMachineId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configAppMachines(ApplicationConfigRequest request) {
        Long appId = request.getAppId();
        Long profileId = request.getProfileId();
        List<Long> machineIdList = request.getMachineIdList();
        // 检查
        for (Long machineId : machineIdList) {
            MachineInfoDO machine = machineInfoService.selectById(machineId);
            Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        }
        // 删除
        SpringHolder.getBean(ApplicationMachineService.class).deleteAppMachineByAppProfileId(appId, profileId);
        // 添加
        for (Long machineId : machineIdList) {
            ApplicationMachineDO machine = new ApplicationMachineDO();
            machine.setAppId(appId);
            machine.setProfileId(profileId);
            machine.setMachineId(machineId);
            applicationMachineDAO.insert(machine);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAppProfileMachine(Long appId, Long profileId, Long targetProfileId) {
        // 删除
        LambdaQueryWrapper<ApplicationMachineDO> deleteWrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, targetProfileId);
        applicationMachineDAO.delete(deleteWrapper);
        // 查询
        LambdaQueryWrapper<ApplicationMachineDO> queryWrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId);
        List<ApplicationMachineDO> machines = applicationMachineDAO.selectList(queryWrapper);
        // 新增
        for (ApplicationMachineDO machine : machines) {
            machine.setId(null);
            machine.setProfileId(targetProfileId);
            machine.setCreateTime(null);
            machine.setUpdateTime(null);
            applicationMachineDAO.insert(machine);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyAppMachine(Long appId, Long targetAppId) {
        // 查询
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId);
        List<ApplicationMachineDO> machines = applicationMachineDAO.selectList(wrapper);
        // 新增
        for (ApplicationMachineDO machine : machines) {
            machine.setId(null);
            machine.setAppId(targetAppId);
            machine.setCreateTime(null);
            machine.setUpdateTime(null);
            applicationMachineDAO.insert(machine);
        }
    }

    @Override
    public void updateAppMachineReleaseId(Long appId, Long profileId, Long releaseId, List<Long> machineIdList) {
        ApplicationMachineDO update = new ApplicationMachineDO();
        update.setReleaseId(releaseId);
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId)
                .in(ApplicationMachineDO::getMachineId, machineIdList);
        applicationMachineDAO.update(update, wrapper);
    }

}
