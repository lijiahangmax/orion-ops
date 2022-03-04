package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.terminal.TerminalConst;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.request.MachineTerminalLogRequest;
import com.orion.ops.entity.request.MachineTerminalRequest;
import com.orion.ops.entity.vo.MachineTerminalLogVO;
import com.orion.ops.entity.vo.MachineTerminalVO;
import com.orion.ops.entity.vo.TerminalAccessVO;
import com.orion.ops.utils.ValueMix;

import java.util.List;
import java.util.Optional;

/**
 * 终端服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/3/31 17:20
 */
public interface MachineTerminalService {

    /**
     * 获取访问配置
     *
     * @param machineId 机器id
     * @return 访问配置
     */
    TerminalAccessVO getAccessConfig(Long machineId);

    /**
     * 获取终端配置
     *
     * @param machineId 机器id
     * @return 配置
     */
    MachineTerminalVO getMachineConfig(Long machineId);

    /**
     * 设置终端配置
     *
     * @param request request
     * @return effect
     */
    Integer updateSetting(MachineTerminalRequest request);

    /**
     * 添加日志
     *
     * @param entity entity
     * @return id
     */
    Long addAccessLog(MachineTerminalLogDO entity);

    /**
     * 更新日志
     *
     * @param token  token
     * @param entity entity
     * @return effect
     */
    Integer updateAccessLog(String token, MachineTerminalLogDO entity);

    /**
     * 查询终端访问日志
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<MachineTerminalLogVO> listAccessLog(MachineTerminalLogRequest request);

    /**
     * 删除终端日志
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteTerminalLog(List<Long> idList);

    /**
     * 通过机器id删除终端配置
     *
     * @param machineId 机器id
     * @return effect
     */
    Integer deleteTerminalByMachineId(Long machineId);

    /**
     * 获取 token 中的 userId
     *
     * @param token token
     * @return userId
     */
    static Long getTokenUserId(String token) {
        return Optional.ofNullable(ValueMix.base62ecbDec(token, TerminalConst.TERMINAL))
                .map(s -> s.split("_"))
                .map(s -> s[0])
                .map(Long::valueOf)
                .orElse(null);
    }

}
