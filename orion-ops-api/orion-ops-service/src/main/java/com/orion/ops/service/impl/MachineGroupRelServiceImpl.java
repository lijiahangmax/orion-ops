package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.ops.dao.MachineGroupRelDAO;
import com.orion.ops.entity.domain.MachineGroupRelDO;
import com.orion.ops.service.api.MachineGroupRelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机器分组关联服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/23 16:11
 */
@Service("machineGroupRelService")
public class MachineGroupRelServiceImpl extends ServiceImpl<MachineGroupRelDAO, MachineGroupRelDO> implements MachineGroupRelService {

    @Resource
    private MachineGroupRelDAO machineGroupRelDAO;

    @Override
    public void addMachineRelByGroup(Long groupId, List<Long> machineIdList) {
        List<MachineGroupRelDO> relList = machineIdList.stream()
                .map(s -> {
                    MachineGroupRelDO rel = new MachineGroupRelDO();
                    rel.setGroupId(groupId);
                    rel.setMachineId(s);
                    return rel;
                }).collect(Collectors.toList());
        // 批量插入
        this.saveBatch(relList);
    }

    @Override
    public void addMachineRelByMachineId(Long machineId, List<Long> groupIdList) {
        List<MachineGroupRelDO> relList = groupIdList.stream()
                .map(s -> {
                    MachineGroupRelDO rel = new MachineGroupRelDO();
                    rel.setMachineId(machineId);
                    rel.setGroupId(s);
                    return rel;
                }).collect(Collectors.toList());
        // 批量插入
        this.saveBatch(relList);
    }

    @Override
    public Integer deleteById(Long id) {
        return machineGroupRelDAO.deleteById(id);
    }

    @Override
    public Integer deleteByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<MachineGroupRelDO> wrapper = new LambdaQueryWrapper<MachineGroupRelDO>()
                .in(MachineGroupRelDO::getMachineId, machineIdList);
        return machineGroupRelDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByGroupIdList(List<Long> groupIdList) {
        LambdaQueryWrapper<MachineGroupRelDO> wrapper = new LambdaQueryWrapper<MachineGroupRelDO>()
                .in(MachineGroupRelDO::getGroupId, groupIdList);
        return machineGroupRelDAO.delete(wrapper);
    }

}
