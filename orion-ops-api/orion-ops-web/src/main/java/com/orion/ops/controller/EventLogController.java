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
package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.user.EventLogRequest;
import com.orion.ops.entity.vo.user.UserEventLogVO;
import com.orion.ops.service.api.UserEventLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 操作日志 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/10 16:17
 */
@Api(tags = "操作日志")
@RestController
@RestWrapper
@RequestMapping("/orion/api/event-log")
public class EventLogController {

    @Resource
    private UserEventLogService userEventLogService;

    @PostMapping("/list")
    @ApiOperation(value = "获取操作日志列表")
    public DataGrid<UserEventLogVO> getLogList(@RequestBody EventLogRequest request) {
        return userEventLogService.getLogList(request);
    }

}
