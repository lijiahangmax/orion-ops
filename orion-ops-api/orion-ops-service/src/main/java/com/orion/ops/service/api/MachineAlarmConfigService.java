package com.orion.ops.service.api;

import com.orion.ops.entity.domain.MachineAlarmConfigDO;
import com.orion.ops.entity.request.machine.MachineAlarmConfigRequest;
import com.orion.ops.entity.vo.machine.MachineAlarmConfigWrapperVO;

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
    MachineAlarmConfigWrapperVO getAlarmConfig(Long machineId);

    /**
     * 设置报警配置
     *
     * @param request request
     */
    void setAlarmConfig(MachineAlarmConfigRequest request);

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
