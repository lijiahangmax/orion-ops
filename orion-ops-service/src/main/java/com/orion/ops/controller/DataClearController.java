package com.orion.ops.controller;

import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.clear.DataClearRange;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.constant.user.RoleType;
import com.orion.ops.entity.request.DataClearRequest;
import com.orion.ops.service.api.DataClearService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @PostMapping("/batch-exec")
    @ApiOperation(value = "清理批量执行记录")
    @EventLog(EventType.DATA_CLEAR_BATCH_EXEC)
    public Integer clearBatchExec(@RequestBody DataClearRequest request) {
        this.validParams(request);
        if (!Currents.isAdministrator()) {
            request.setICreated(Const.ENABLE);
        }
        return dataClearService.clearBatchExec(request);
    }

    @PostMapping("/terminal-log")
    @ApiOperation(value = "清理终端日志记录")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.DATA_CLEAR_TERMINAL_LOG)
    public Integer clearTerminalLog(@RequestBody DataClearRequest request) {
        this.validParams(request);
        return dataClearService.clearTerminalLog(request);
    }

    @PostMapping("/scheduler-record")
    @ApiOperation(value = "清理调度任务记录")
    @EventLog(EventType.DATA_CLEAR_SCHEDULER_RECORD)
    public Integer clearSchedulerRecord(@RequestBody DataClearRequest request) {
        this.validParams(request);
        return dataClearService.clearSchedulerRecord(request);
    }

    @PostMapping("/app-build")
    @ApiOperation(value = "清理应用构建记录")
    @EventLog(EventType.DATA_CLEAR_APP_BUILD)
    public Integer clearAppBuild(@RequestBody DataClearRequest request) {
        this.validParams(request);
        Valid.notNull(request.getProfileId());
        return dataClearService.clearAppBuild(request);
    }

    @PostMapping("/app-release")
    @ApiOperation(value = "清理应用发布记录")
    @EventLog(EventType.DATA_CLEAR_APP_RELEASE)
    public Integer clearAppRelease(@RequestBody DataClearRequest request) {
        this.validParams(request);
        Valid.notNull(request.getProfileId());
        return dataClearService.clearAppRelease(request);
    }

    @PostMapping("/app-pipeline")
    @ApiOperation(value = "清理应用流水线任务记录")
    @EventLog(EventType.DATA_CLEAR_APP_PIPELINE)
    public Integer clearAppPipeline(@RequestBody DataClearRequest request) {
        this.validParams(request);
        Valid.notNull(request.getProfileId());
        return dataClearService.clearAppPipeline(request);
    }

    @PostMapping("/event-log")
    @ApiOperation(value = "清理用户操作日志")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.DATA_CLEAR_EVENT_LOG)
    public Integer clearEventLog(@RequestBody DataClearRequest request) {
        this.validParams(request);
        return dataClearService.clearEventLog(request);
    }

    /**
     * 验证参数
     *
     * @param request request
     */
    private void validParams(DataClearRequest request) {
        DataClearRange range = Valid.notNull(DataClearRange.of(request.getRange()));
        switch (range) {
            case DAY:
                Valid.gte(request.getReserveDay(), 0);
                return;
            case TOTAL:
                Valid.gte(request.getReserveTotal(), 0);
                return;
            case REL_ID:
                Valid.notEmpty(request.getRelIdList());
                return;
            default:
        }
    }

}
