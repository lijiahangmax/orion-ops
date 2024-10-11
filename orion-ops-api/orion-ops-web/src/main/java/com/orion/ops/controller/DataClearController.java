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

import com.orion.lang.utils.Exceptions;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.DataClearRange;
import com.orion.ops.constant.DataClearType;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.data.DataClearRequest;
import com.orion.ops.service.api.DataClearService;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 数据清理 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/24 16:50
 */
@Api(tags = "数据清理")
@RestController
@RestWrapper
@RequestMapping("/orion/api/data-clear")
public class DataClearController {

    @Resource
    private DataClearService dataClearService;

    @PostMapping("/clear")
    @ApiOperation(value = "清理数据")
    @EventLog(EventType.DATA_CLEAR)
    public Integer clearData(@RequestBody DataClearRequest request) {
        // 检查参数
        DataClearType dataClearType = this.validParams(request);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.LABEL, dataClearType.getLabel());
        // 清理
        switch (dataClearType) {
            case BATCH_EXEC:
                return dataClearService.clearBatchExec(request);
            case TERMINAL_LOG:
                return dataClearService.clearTerminalLog(request);
            case SCHEDULER_RECORD:
                return dataClearService.clearSchedulerRecord(request);
            case APP_BUILD:
                Valid.notNull(request.getProfileId());
                return dataClearService.clearAppBuild(request);
            case APP_RELEASE:
                Valid.notNull(request.getProfileId());
                return dataClearService.clearAppRelease(request);
            case APP_PIPELINE_EXEC:
                Valid.notNull(request.getProfileId());
                return dataClearService.clearAppPipeline(request);
            case USER_EVENT_LOG:
                return dataClearService.clearEventLog(request);
            case MACHINE_ALARM_HISTORY:
                Valid.notNull(request.getMachineId());
                return dataClearService.clearMachineAlarmHistory(request);
            default:
                throw Exceptions.unsupported();
        }
    }

    /**
     * 验证参数
     *
     * @param request request
     * @return clear type
     */
    private DataClearType validParams(DataClearRequest request) {
        DataClearRange range = Valid.notNull(DataClearRange.of(request.getRange()));
        switch (range) {
            case DAY:
                Valid.gte(request.getReserveDay(), 0);
                break;
            case TOTAL:
                Valid.gte(request.getReserveTotal(), 0);
                break;
            case REL_ID:
                Valid.notEmpty(request.getRelIdList());
                break;
            default:
        }
        return Valid.notNull(DataClearType.of(request.getClearType()));
    }

}
