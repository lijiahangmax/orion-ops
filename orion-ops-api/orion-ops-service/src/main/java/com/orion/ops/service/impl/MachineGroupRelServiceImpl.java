package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.ops.constant.KeyConst;
import com.orion.ops.dao.MachineGroupRelDAO;
import com.orion.ops.entity.domain.MachineGroupRelDO;
import com.orion.ops.entity.request.machine.MachineGroupRelRequest;
import com.orion.ops.service.api.MachineGroupRelService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMachineRelByGroup(Long groupId, List<Long> machineIdList) {
        // 排重
        LambdaQueryWrapper<MachineGroupRelDO> wrapper = new LambdaQueryWrapper<MachineGroupRelDO>()
                .eq(MachineGroupRelDO::getGroupId, groupId);
        machineGroupRelDAO.selectList(wrapper)
                .forEach(m -> machineIdList.removeIf(m.getMachineId()::equals));
        if (machineIdList.isEmpty()) {
            return;
        }
        // 批量插入
        List<MachineGroupRelDO> relList = machineIdList.stream()
                .map(s -> {
                    MachineGroupRelDO rel = new MachineGroupRelDO();
                    rel.setGroupId(groupId);
                    rel.setMachineId(s);
                    return rel;
                }).collect(Collectors.toList());
        this.saveBatch(relList);
        // 清理缓存
        this.clearGroupRelCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMachineGroup(Long machineId, List<Long> groupIdList) {
        // 删除
        LambdaQueryWrapper<MachineGroupRelDO> wrapper = new LambdaQueryWrapper<MachineGroupRelDO>()
                .eq(MachineGroupRelDO::getMachineId, machineId);
        machineGroupRelDAO.delete(wrapper);
        // 批量插入
        List<MachineGroupRelDO> relList = groupIdList.stream()
                .map(s -> {
                    MachineGroupRelDO rel = new MachineGroupRelDO();
                    rel.setMachineId(machineId);
                    rel.setGroupId(s);
                    return rel;
                }).collect(Collectors.toList());
        this.saveBatch(relList);
        // 清理缓存
        this.clearGroupRelCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveMachineRel(MachineGroupRelRequest request) {
        // 删除组内id
        Long groupId = request.getGroupId();
        List<Long> machineIdList = request.getMachineIdList();
        LambdaQueryWrapper<MachineGroupRelDO> deleteWrapper = new LambdaQueryWrapper<MachineGroupRelDO>()
                .eq(MachineGroupRelDO::getGroupId, groupId)
                .in(MachineGroupRelDO::getMachineId, machineIdList);
        machineGroupRelDAO.delete(deleteWrapper);
        // 目标去重
        LambdaQueryWrapper<MachineGroupRelDO> queryWrapper = new LambdaQueryWrapper<MachineGroupRelDO>()
                .eq(MachineGroupRelDO::getGroupId, request.getTargetGroupId());
        machineGroupRelDAO.selectList(queryWrapper)
                .forEach(m -> machineIdList.removeIf(m.getMachineId()::equals));
        // 批量插入
        if (!machineIdList.isEmpty()) {
            List<MachineGroupRelDO> relList = machineIdList.stream()
                    .map(m -> {
                        MachineGroupRelDO rel = new MachineGroupRelDO();
                        rel.setMachineId(m);
                        rel.setGroupId(request.getTargetGroupId());
                        return rel;
                    }).collect(Collectors.toList());
            this.saveBatch(relList);
        }
        // 清理缓存
        this.clearGroupRelCache();
    }

    @Override
    public Integer deleteByGroupMachineId(Long groupId, List<Long> machineIdList) {
        // 删除数据库
        LambdaQueryWrapper<MachineGroupRelDO> wrapper = new LambdaQueryWrapper<MachineGroupRelDO>()
                .eq(MachineGroupRelDO::getGroupId, groupId)
                .in(MachineGroupRelDO::getMachineId, machineIdList);
        int effect = machineGroupRelDAO.delete(wrapper);
        // 清理缓存
        this.clearGroupRelCache();
        return effect;
    }

    @Override
    public Integer deleteByMachineIdList(List<Long> machineIdList) {
        // 删除数据库
        LambdaQueryWrapper<MachineGroupRelDO> wrapper = new LambdaQueryWrapper<MachineGroupRelDO>()
                .in(MachineGroupRelDO::getMachineId, machineIdList);
        int effect = machineGroupRelDAO.delete(wrapper);
        // 清理缓存
        this.clearGroupRelCache();
        return effect;
    }

    @Override
    public Integer deleteByGroupIdList(List<Long> groupIdList) {
        // 删除数据库
        LambdaQueryWrapper<MachineGroupRelDO> wrapper = new LambdaQueryWrapper<MachineGroupRelDO>()
                .in(MachineGroupRelDO::getGroupId, groupIdList);
        int effect = machineGroupRelDAO.delete(wrapper);
        // 清理缓存
        this.clearGroupRelCache();
        return effect;
    }

    @Override
    public Map<Long, List<Long>> getMachineRelByCache() {
        // 查询缓存
        // String cacheData = redisTemplate.opsForValue().get(KeyConst.MACHINE_GROUP_REL_KEY);
        // if (!Strings.isBlank(cacheData)) {
        //     return JSON.parseObject(cacheData, new TypeReference<Map<Long, List<Long>>>() {
        //     });
        // }
        // 查询数据
        Map<Long, List<Long>> groupMachines = machineGroupRelDAO.selectList(null)
                .stream().collect(Collectors.groupingBy(MachineGroupRelDO::getMachineId,
                        Collectors.mapping(MachineGroupRelDO::getGroupId, Collectors.toList())));
        // 设置缓存
        redisTemplate.opsForValue().set(KeyConst.MACHINE_GROUP_REL_KEY,
                JSON.toJSONString(groupMachines),
                KeyConst.MACHINE_GROUP_REL_EXPIRE,
                TimeUnit.SECONDS);
        return groupMachines;
    }

    @Override
    public void clearGroupRelCache() {
        redisTemplate.delete(KeyConst.MACHINE_GROUP_REL_KEY);
    }

}
