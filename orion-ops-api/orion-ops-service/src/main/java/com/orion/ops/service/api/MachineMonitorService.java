package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.request.machine.MachineMonitorRequest;
import com.orion.ops.entity.vo.machine.MachineMonitorVO;

import java.util.List;

/**
 * 机器监控服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 18:46
 */
public interface MachineMonitorService {

    /**
     * 通过 机器id 查询
     *
     * @param machineId machineId
     * @return row
     */
    MachineMonitorDO selectByMachineId(Long machineId);

    /**
     * 获取监控列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<MachineMonitorVO> getMonitorList(MachineMonitorRequest request);

    /**
     * 获取监控配置
     *
     * @param machineId machineId
     * @return 配置信息
     */
    MachineMonitorVO getMonitorConfig(Long machineId);

    /**
     * 更新监控配置
     *
     * @param request request
     * @return status
     */
    MachineMonitorVO updateMonitorConfig(MachineMonitorRequest request);

    /**
     * 更新监控配置
     *
     * @param machineId machineId
     * @param update    update
     * @return effect
     */
    Integer updateMonitorConfigByMachineId(Long machineId, MachineMonitorDO update);

    /**
     * 通过机器 id 删除
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

    /**
     * 安装监控插件
     *
     * @param machineId machineId
     * @param upgrade   upgrade
     * @return status
     */
    MachineMonitorVO installMonitorAgent(Long machineId, boolean upgrade);

    /**
     * 检测监控插件状态
     *
     * @param machineId machineId
     * @return status
     */
    MachineMonitorVO checkMonitorStatus(Long machineId);

    /**
     * 获取版本
     *
     * @param url         url
     * @param accessToken accessToken
     * @return version
     */
    String getMonitorVersion(String url, String accessToken);

    /**
     * 同步机器插件信息
     *
     * @param machineId   machineId
     * @param url         url
     * @param accessToken accessToken
     * @return version
     */
    String syncMonitorAgent(Long machineId, String url, String accessToken);

}
