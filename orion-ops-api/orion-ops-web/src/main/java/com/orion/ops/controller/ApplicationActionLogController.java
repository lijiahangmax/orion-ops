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

import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.app.ApplicationActionLogRequest;
import com.orion.ops.entity.vo.app.ApplicationActionLogVO;
import com.orion.ops.service.api.ApplicationActionLogService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 应用操作日志api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/9 14:42
 */
@Api(tags = "应用操作日志")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-action-log")
public class ApplicationActionLogController {

    @Resource
    private ApplicationActionLogService applicationActionLogService;

    @PostMapping("/detail")
    @ApiOperation(value = "获取日志详情")
    public ApplicationActionLogVO getActionDetail(@RequestBody ApplicationActionLogRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationActionLogService.getDetailById(id);
    }

    @IgnoreLog
    @PostMapping("/status")
    @ApiOperation(value = "获取日志状态")
    public ApplicationActionLogVO getActionStatus(@RequestBody ApplicationActionLogRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationActionLogService.getStatusById(id);
    }

}
