package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.dao.ApplicationMachineDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.ApplicationMachineDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.ApplicationConfigRequest;
import com.orion.ops.entity.vo.ApplicationMachineVO;
import com.orion.ops.service.api.ApplicationMachineService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.spring.SpringHolder;
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
    private MachineInfoDAO machineInfoDAO;

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
    public List<Long> getAppProfileMachineIdList(Long appId, Long profileId, boolean filterMachineStatus) {
        LambdaQueryWrapper<ApplicationMachineDO> appMachineWrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId);
        List<Long> appMachineIdList = applicationMachineDAO.selectList(appMachineWrapper).stream()
                .map(ApplicationMachineDO::getMachineId)
                .collect(Collectors.toList());
        if (!filterMachineStatus || appMachineIdList.isEmpty()) {
            return appMachineIdList;
        }
        // 过滤进行中的机器
        LambdaQueryWrapper<MachineInfoDO> machineWrapper = new LambdaQueryWrapper<MachineInfoDO>()
                .eq(MachineInfoDO::getMachineStatus, Const.ENABLE)
                .in(MachineInfoDO::getId, appMachineIdList);
        return machineInfoDAO.selectList(machineWrapper).stream()
                .map(MachineInfoDO::getId)
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
    public List<ApplicationMachineVO> getAppProfileMachineDetail(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId);
        // 查询关联
        List<ApplicationMachineVO> list = DataQuery.of(applicationMachineDAO)
                .wrapper(wrapper)
                .list(ApplicationMachineVO.class);
        // 查询机器
        list.forEach(m -> Optional.ofNullable(m.getMachineId())
                .map(machineInfoDAO::selectById)
                .ifPresent(s -> {
                    m.setMachineName(s.getMachineName());
                    m.setMachineHost(s.getMachineHost());
                    m.setMachineTag(s.getMachineTag());
                }));
        return list;
    }

    @Override
    public List<ApplicationMachineDO> getAppProfileMachineList(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, appId)
                .eq(ApplicationMachineDO::getProfileId, profileId);
        return applicationMachineDAO.selectList(wrapper);
    }

    @Override
    public Integer deleteAppMachineByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .in(ApplicationMachineDO::getMachineId, machineIdList);
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
    public Integer deleteById(Long id) {
        return applicationMachineDAO.deleteById(id);
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
            MachineInfoDO machine = machineInfoDAO.selectById(machineId);
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
