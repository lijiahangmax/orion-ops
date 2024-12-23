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
import cn.orionsec.ops.entity.domain.MachineMonitorDO;
import cn.orionsec.ops.entity.request.machine.MachineMonitorRequest;
import cn.orionsec.ops.entity.vo.machine.MachineMonitorVO;

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
