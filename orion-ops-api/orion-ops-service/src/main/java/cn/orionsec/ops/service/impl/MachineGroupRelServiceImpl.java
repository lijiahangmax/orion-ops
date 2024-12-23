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

import cn.orionsec.ops.constant.KeyConst;
import cn.orionsec.ops.dao.MachineGroupRelDAO;
import cn.orionsec.ops.entity.domain.MachineGroupRelDO;
import cn.orionsec.ops.service.api.MachineGroupRelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public Integer deleteByGroupMachineId(List<Long> groupIdList, List<Long> machineIdList) {
        // 删除数据库
        LambdaQueryWrapper<MachineGroupRelDO> wrapper = new LambdaQueryWrapper<MachineGroupRelDO>()
                .in(MachineGroupRelDO::getGroupId, groupIdList)
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
