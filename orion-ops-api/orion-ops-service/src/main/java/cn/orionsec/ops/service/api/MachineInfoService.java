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
package cn.orionsec.ops.service.api;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.net.host.SessionStore;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.request.machine.MachineInfoRequest;
import cn.orionsec.ops.entity.vo.machine.MachineInfoVO;

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
     */
    void testPing(Long id);

    /**
     * 尝试ping 主机
     *
     * @param host host
     */
    void testPing(String host);

    /**
     * 尝试连接 主机
     *
     * @param id id
     */
    void testConnect(Long id);

    /**
     * 尝试连接 主机
     *
     * @param request request
     */
    void testConnect(MachineInfoRequest request);

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
