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

import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.machine.MachineAlarmType;
import cn.orionsec.ops.dao.MachineAlarmConfigDAO;
import cn.orionsec.ops.dao.MachineAlarmGroupDAO;
import cn.orionsec.ops.entity.domain.MachineAlarmConfigDO;
import cn.orionsec.ops.entity.domain.MachineAlarmGroupDO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.domain.MachineMonitorDO;
import cn.orionsec.ops.entity.request.machine.MachineAlarmConfigRequest;
import cn.orionsec.ops.entity.vo.machine.MachineAlarmConfigVO;
import cn.orionsec.ops.entity.vo.machine.MachineAlarmConfigWrapperVO;
import cn.orionsec.ops.service.api.MachineAlarmConfigService;
import cn.orionsec.ops.service.api.MachineAlarmGroupService;
import cn.orionsec.ops.service.api.MachineInfoService;
import cn.orionsec.ops.service.api.MachineMonitorService;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.Valid;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.convert.Converts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机器报警配置服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:52
 */
@Service("machineAlarmConfigService")
public class MachineAlarmConfigServiceImpl implements MachineAlarmConfigService {

    @Resource
    private MachineAlarmConfigDAO machineAlarmConfigDAO;

    @Resource
    private MachineAlarmGroupDAO machineAlarmGroupDAO;

    @Resource
    private MachineAlarmGroupService machineAlarmGroupService;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private MachineMonitorService machineMonitorService;

    @Override
    public MachineAlarmConfigWrapperVO getAlarmConfigInfo(Long machineId) {
        MachineAlarmConfigWrapperVO wrapper = new MachineAlarmConfigWrapperVO();
        // 查询配置
        List<MachineAlarmConfigDO> config = this.selectByMachineId(machineId);
        List<MachineAlarmConfigVO> alarmConfig = Converts.toList(config, MachineAlarmConfigVO.class);
        // 查询报警组
        List<MachineAlarmGroupDO> group = machineAlarmGroupService.selectByMachineId(machineId);
        List<Long> groupIdList = group.stream()
                .map(MachineAlarmGroupDO::getGroupId)
                .collect(Collectors.toList());
        wrapper.setConfig(alarmConfig);
        wrapper.setGroupIdList(groupIdList);
        return wrapper;
    }

    @Override
    public List<MachineAlarmConfigVO> getAlarmConfig(Long machineId) {
        // 查询配置
        List<MachineAlarmConfigDO> config = this.selectByMachineId(machineId);
        return Converts.toList(config, MachineAlarmConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setAlarmConfig(MachineAlarmConfigRequest request) {
        // 查询机器信息
        Long machineId = request.getMachineId();
        MachineInfoDO machine = machineInfoService.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        // 删除配置
        Integer type = request.getType();
        LambdaQueryWrapper<MachineAlarmConfigDO> wrapper = new LambdaQueryWrapper<MachineAlarmConfigDO>()
                .eq(MachineAlarmConfigDO::getMachineId, machineId)
                .eq(MachineAlarmConfigDO::getAlarmType, type);
        machineAlarmConfigDAO.delete(wrapper);
        // 插入配置
        MachineAlarmConfigDO config = new MachineAlarmConfigDO();
        config.setMachineId(machineId);
        config.setAlarmType(type);
        config.setAlarmThreshold(request.getAlarmThreshold());
        config.setTriggerThreshold(request.getTriggerThreshold());
        config.setNotifySilence(request.getNotifySilence());
        machineAlarmConfigDAO.insert(config);
        // 同步报警配置
        MachineMonitorDO machineMonitor = machineMonitorService.selectByMachineId(machineId);
        if (machineMonitor != null) {
            machineMonitorService.syncMonitorAgent(machineId, machineMonitor.getMonitorUrl(), machineMonitor.getAccessToken());
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.NAME, machine.getMachineName());
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, machineId);
        EventParamsHolder.addParam(EventKeys.LABEL, MachineAlarmType.of(type).getLabel());
        EventParamsHolder.addParams(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setAlarmGroup(Long machineId, List<Long> groupIdList) {
        // 查询机器信息
        MachineInfoDO machine = machineInfoService.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        // 删除报警组
        machineAlarmGroupService.deleteByMachineId(machineId);
        // 插入报警组
        groupIdList.stream()
                .map(g -> {
                    MachineAlarmGroupDO group = new MachineAlarmGroupDO();
                    group.setMachineId(machineId);
                    group.setGroupId(g);
                    return group;
                }).forEach(machineAlarmGroupDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.NAME, machine.getMachineName());
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, machineId);
        EventParamsHolder.addParam(EventKeys.ID_LIST, groupIdList);
    }

    @Override
    public List<MachineAlarmConfigDO> selectByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineAlarmConfigDO> wrapper = new LambdaQueryWrapper<MachineAlarmConfigDO>()
                .eq(MachineAlarmConfigDO::getMachineId, machineId);
        return machineAlarmConfigDAO.selectList(wrapper);
    }

    @Override
    public Integer selectCountByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineAlarmConfigDO> wrapper = new LambdaQueryWrapper<MachineAlarmConfigDO>()
                .eq(MachineAlarmConfigDO::getMachineId, machineId);
        return machineAlarmConfigDAO.selectCount(wrapper);
    }

    @Override
    public Integer deleteByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineAlarmConfigDO> wrapper = new LambdaQueryWrapper<MachineAlarmConfigDO>()
                .eq(MachineAlarmConfigDO::getMachineId, machineId);
        return machineAlarmConfigDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<MachineAlarmConfigDO> wrapper = new LambdaQueryWrapper<MachineAlarmConfigDO>()
                .in(MachineAlarmConfigDO::getMachineId, machineIdList);
        return machineAlarmConfigDAO.delete(wrapper);
    }

}
