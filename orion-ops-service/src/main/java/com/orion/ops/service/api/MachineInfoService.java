package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.net.remote.channel.SessionStore;
import com.orion.ops.consts.machine.MachineProperties;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.MachineInfoRequest;
import com.orion.ops.entity.vo.MachineInfoVO;

import java.util.List;

/**
 * 机器管理service
 *
 * @author Jiahang Li
 * @since 2021-03-29
 */
public interface MachineInfoService {

    /**
     * 添加机器
     *
     * @param request request
     * @return id
     */
    Long addMachine(MachineInfoRequest request);

    /**
     * 修改机器
     *
     * @param request request
     * @return effect
     */
    Integer updateMachine(MachineInfoRequest request);

    /**
     * 删除机器
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteMachine(List<Long> idList);

    /**
     * 更新机器状态
     *
     * @param idList idList
     * @param status status
     * @return effect
     */
    Integer updateStatus(List<Long> idList, Integer status);

    /**
     * 机器列表
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<MachineInfoVO> listMachine(MachineInfoRequest request);

    /**
     * 机器详情
     *
     * @param id id
     * @return dataGrid
     */
    MachineInfoVO machineDetail(Long id);

    /**
     * 复制机器机器
     *
     * @param id id
     * @return id
     */
    Long copyMachine(Long id);

    /**
     * 通过id查询机器
     *
     * @param id id
     * @return row
     */
    MachineInfoDO selectById(Long id);

    /**
     * 尝试ping 主机
     *
     * @param id id
     * @return 1正常 2异常
     */
    Integer testPing(Long id);

    /**
     * 尝试连接 主机
     *
     * @param id id
     * @return 1正常 2异常
     */
    Integer testConnect(Long id);

    /**
     * 建立远程连接
     *
     * @param id id
     * @return session
     */
    SessionStore openSessionStore(Long id);

    /**
     * 建立远程连接
     *
     * @param machine machine
     * @return session
     */
    SessionStore openSessionStore(MachineInfoDO machine);

    /**
     * 同步执行命令获取输出结果
     *
     * @param id       机器id
     * @param property property
     * @return result
     */
    String getPropertiesResultSync(Long id, MachineProperties property);

    /**
     * 同步执行命令获取输出结果
     *
     * @param id      机器id
     * @param command 命令
     * @return result
     */
    String getCommandResultSync(Long id, String command);

    /**
     * 获取机器名称
     *
     * @param id id
     * @return name
     */
    String getMachineName(Long id);

}
