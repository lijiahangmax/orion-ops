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
import cn.orionsec.ops.entity.request.alarm.AlarmGroupRequest;
import cn.orionsec.ops.entity.vo.alarm.AlarmGroupVO;

/**
 * 报警组服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/25 15:37
 */
public interface AlarmGroupService {

    /**
     * 添加报警组
     *
     * @param request request
     * @return id
     */
    Long addAlarmGroup(AlarmGroupRequest request);

    /**
     * 更新报警组
     *
     * @param request request
     * @return effect
     */
    Integer updateAlarmGroup(AlarmGroupRequest request);

    /**
     * 删除报警组
     *
     * @param id id
     * @return effect
     */
    Integer deleteAlarmGroup(Long id);

    /**
     * 查询列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<AlarmGroupVO> getAlarmGroupList(AlarmGroupRequest request);

    /**
     * 查询详情
     *
     * @param id id
     * @return row
     */
    AlarmGroupVO getAlarmGroupDetail(Long id);

}
