package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.ApplicationMachineDAO;
import com.orion.ops.entity.domain.ApplicationMachineDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.vo.ApplicationMachineVO;
import com.orion.ops.service.api.ApplicationMachineService;
import com.orion.ops.service.api.MachineInfoService;
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

    @Override
    public Long getAppProfileMachineId(Long id, Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getId, id)
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
    public List<ApplicationMachineVO> getAppProfileMachineList(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId);
        return applicationMachineDAO.selectList(wrapper)
                .stream()
                .map(m -> {
                    MachineInfoDO machine = machineInfoService.selectById(m.getMachineId());
                    ApplicationMachineVO machineVO = Converts.to(machine, ApplicationMachineVO.class);
                    machineVO.setId(m.getId());
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
    public Integer selectAppProfileMachineCount(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId);
        return applicationMachineDAO.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAppProfileMachine(Long appId, Long profileId, Long syncProfileId) {
        // 删除
        LambdaQueryWrapper<ApplicationMachineDO> deleteWrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, syncProfileId);
        applicationMachineDAO.delete(deleteWrapper);
        // 查询
        LambdaQueryWrapper<ApplicationMachineDO> queryWrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId);
        List<ApplicationMachineDO> machines = applicationMachineDAO.selectList(queryWrapper);
        // 新增
        for (ApplicationMachineDO machine : machines) {
            machine.setId(null);
            machine.setCreateTime(null);
            machine.setUpdateTime(null);
            machine.setProfileId(syncProfileId);
            applicationMachineDAO.insert(machine);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyAppMachine(Long appId, Long targetAppId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId);
        List<ApplicationMachineDO> machines = applicationMachineDAO.selectList(wrapper);
        machines.forEach(s -> {
            s.setId(null);
            s.setAppId(targetAppId);
            s.setCreateTime(null);
            s.setUpdateTime(null);
        });
        machines.forEach(applicationMachineDAO::insert);
    }

}
