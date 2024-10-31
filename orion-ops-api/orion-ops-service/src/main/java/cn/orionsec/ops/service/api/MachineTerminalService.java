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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.entity.domain.MachineTerminalLogDO;
import cn.orionsec.ops.entity.request.machine.MachineTerminalLogRequest;
import cn.orionsec.ops.entity.request.machine.MachineTerminalRequest;
import cn.orionsec.ops.entity.vo.machine.MachineTerminalLogVO;
import cn.orionsec.ops.entity.vo.machine.MachineTerminalVO;
import cn.orionsec.ops.entity.vo.machine.TerminalAccessVO;

import java.util.List;

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
    Long addTerminalLog(MachineTerminalLogDO entity);

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
     * @param machineIdList 机器id
     * @return effect
     */
    Integer deleteTerminalByMachineIdList(List<Long> machineIdList);

    /**
     * 获取下载 terminal 录屏路径
     *
     * @param id id
     * @return path
     */
    String getTerminalScreenFilePath(Long id);

}
