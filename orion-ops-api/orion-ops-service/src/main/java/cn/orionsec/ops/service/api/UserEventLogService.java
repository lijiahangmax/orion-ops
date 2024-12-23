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
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.user.EventLogRequest;
import cn.orionsec.ops.entity.vo.user.UserEventLogVO;

/**
 * 用户操作日志服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/22 20:19
 */
public interface UserEventLogService {

    /**
     * 记录日志
     *
     * @param eventType 操作
     * @param isSuccess 是否成功
     */
    void recordLog(EventType eventType, boolean isSuccess);

    /**
     * 获取操作日志
     *
     * @param request request
     * @return rows
     */
    DataGrid<UserEventLogVO> getLogList(EventLogRequest request);

}
