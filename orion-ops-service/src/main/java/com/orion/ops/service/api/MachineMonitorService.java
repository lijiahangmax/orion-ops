package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.request.MachineMonitorRequest;
import com.orion.ops.entity.vo.MachineMonitorVO;

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
     * @return effect
     */
    Integer updateMonitorConfig(MachineMonitorRequest request);

    /**
     * 测试连接监控插件
     *
     * @param url         url
     * @param accessToken accessToken
     * @return wrapper
     */
    HttpWrapper<Integer> testPingMonitor(String url, String accessToken);

    /**
     * 通过机器 id 删除
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

}
