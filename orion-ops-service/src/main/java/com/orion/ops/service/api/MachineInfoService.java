package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.MachineInfoRequest;
import com.orion.ops.entity.vo.MachineInfoVO;
import com.orion.remote.channel.SessionStore;

/**
 * 机器管理service
 *
 * @author Jiahang Li
 * @since 2021-03-29
 */
public interface MachineInfoService {

    /**
     * 添加或修改机器
     *
     * @param request request
     * @return id/effect
     */
    Long addUpdateMachine(MachineInfoRequest request);

    /**
     * 删除机器
     *
     * @param request request
     * @return effect
     */
    Integer deleteMachine(MachineInfoRequest request);

    /**
     * 机器列表
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<MachineInfoVO> listMachine(MachineInfoRequest request);

    /**
     * 同步属性
     *
     * @param request request
     * @return value
     */
    String syncProperties(MachineInfoRequest request);

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
    SessionStore getSessionStore(Long id);

    /**
     * 执行命令获取输出结果
     *
     * @param id      机器id
     * @param command 命令
     * @return result
     */
    String getCommandResult(Long id, String command);

}
