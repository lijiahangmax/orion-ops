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
package cn.orionsec.ops.service.api;

import cn.orionsec.ops.entity.domain.MachineAlarmConfigDO;
import cn.orionsec.ops.entity.request.machine.MachineAlarmConfigRequest;
import cn.orionsec.ops.entity.vo.machine.MachineAlarmConfigVO;
import cn.orionsec.ops.entity.vo.machine.MachineAlarmConfigWrapperVO;

import java.util.List;

/**
 * 机器报警配置服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:52
 */
public interface MachineAlarmConfigService {

    /**
     * 获取报警配置
     *
     * @param machineId machineId
     * @return config
     */
    MachineAlarmConfigWrapperVO getAlarmConfigInfo(Long machineId);

    /**
     * 获取报警配置
     *
     * @param machineId machineId
     * @return config
     */
    List<MachineAlarmConfigVO> getAlarmConfig(Long machineId);

    /**
     * 设置报警配置
     *
     * @param request request
     */
    void setAlarmConfig(MachineAlarmConfigRequest request);

    /**
     * 设置报警联系组
     *
     * @param machineId   machineId
     * @param groupIdList groupIdList
     */
    void setAlarmGroup(Long machineId, List<Long> groupIdList);

    /**
     * 通过机器id查询
     *
     * @param machineId machineId
     * @return rows
     */
    List<MachineAlarmConfigDO> selectByMachineId(Long machineId);

    /**
     * 通过机器id查询数量
     *
     * @param machineId machineId
     * @return count
     */
    Integer selectCountByMachineId(Long machineId);

    /**
     * 通过机器id删除
     *
     * @param machineId machineId
     * @return effect
     */
    Integer deleteByMachineId(Long machineId);

    /**
     * 通过机器id删除
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

}
