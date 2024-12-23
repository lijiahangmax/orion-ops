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
package cn.orionsec.ops.handler.exporter;

import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.ExportType;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.dao.UserEventLogDAO;
import cn.orionsec.ops.entity.domain.UserEventLogDO;
import cn.orionsec.ops.entity.exporter.EventLogExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;
import cn.orionsec.ops.utils.Currents;
import cn.orionsec.ops.utils.EventParamsHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 用户操作日志 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:54
 */
public class UserEventLogDataExporter extends AbstractDataExporter<EventLogExportDTO> {

    private static final UserEventLogDAO userEventLogDAO = SpringHolder.getBean(UserEventLogDAO.class);

    public UserEventLogDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.USER_EVENT_LOG, request, response);
        // 设置用户id
        if (Currents.isAdministrator()) {
            if (Const.ENABLE.equals(request.getOnlyMyself())) {
                request.setUserId(Currents.getUserId());
            }
        } else {
            request.setUserId(Currents.getUserId());
        }
    }

    @Override
    protected List<EventLogExportDTO> queryData() {
        // 查询数据
        Long userId = request.getUserId();
        Integer classify = request.getClassify();
        LambdaQueryWrapper<UserEventLogDO> wrapper = new LambdaQueryWrapper<UserEventLogDO>()
                .eq(UserEventLogDO::getExecResult, Const.ENABLE)
                .eq(Objects.nonNull(userId), UserEventLogDO::getUserId, userId)
                .eq(Objects.nonNull(classify), UserEventLogDO::getEventClassify, classify)
                .orderByDesc(UserEventLogDO::getCreateTime);
        List<UserEventLogDO> logList = userEventLogDAO.selectList(wrapper);
        return Converts.toList(logList, EventLogExportDTO.class);
    }

    @Override
    protected void setEventParams() {
        super.setEventParams();
        EventParamsHolder.addParam(EventKeys.USER_ID, request.getUserId());
        EventParamsHolder.addParam(EventKeys.CLASSIFY, request.getClassify());
    }

}
