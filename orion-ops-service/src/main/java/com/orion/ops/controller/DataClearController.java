package com.orion.ops.controller;

import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.clear.DataClearRange;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.sftp.DataClearRequest;
import com.orion.ops.service.api.DataClearService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 数据清理
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/24 16:50
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/data-clear")
public class DataClearController {

    @Resource
    private DataClearService dataClearService;

    /**
     * 清理 批量执行
     */
    @RequestMapping("/batch-exec")
    @EventLog(EventType.DATA_CLEAR_BATCH_EXEC)
    public Integer clearBatchExec(@RequestBody DataClearRequest request) {
        this.validParams(request);
        if (!Currents.isAdministrator()) {
            request.setICreated(Const.ENABLE);
        }
        return dataClearService.clearBatchExec(request);
    }

    /**
     * 清理 终端日志
     */
    @RequestMapping("/terminal-log")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.DATA_CLEAR_TERMINAL_LOG)
    public Integer clearTerminalLog(@RequestBody DataClearRequest request) {
        this.validParams(request);
        return dataClearService.clearTerminalLog(request);
    }

    /**
     * 清理 调度记录
     */
    @RequestMapping("/scheduler-record")
    @EventLog(EventType.DATA_CLEAR_SCHEDULER_RECORD)
    public Integer clearSchedulerRecord(@RequestBody DataClearRequest request) {
        this.validParams(request);
        return dataClearService.clearSchedulerRecord(request);
    }

    /**
     * 清理 应用构建
     */
    @RequestMapping("/app-build")
    @EventLog(EventType.DATA_CLEAR_APP_BUILD)
    public Integer clearAppBuild(@RequestBody DataClearRequest request) {
        this.validParams(request);
        Valid.notNull(request.getProfileId());
        return dataClearService.clearAppBuild(request);
    }

    /**
     * 清理 应用发布
     */
    @RequestMapping("/app-release")
    @EventLog(EventType.DATA_CLEAR_APP_RELEASE)
    public Integer clearAppRelease(@RequestBody DataClearRequest request) {
        this.validParams(request);
        Valid.notNull(request.getProfileId());
        return dataClearService.clearAppRelease(request);
    }

    /**
     * 清理 应用流水线
     */
    @RequestMapping("/app-pipeline")
    @EventLog(EventType.DATA_CLEAR_APP_PIPELINE)
    public Integer clearAppPipeline(@RequestBody DataClearRequest request) {
        this.validParams(request);
        Valid.notNull(request.getProfileId());
        return dataClearService.clearAppPipeline(request);
    }

    /**
     * 清理 站内信
     */
    @RequestMapping("/web-side-message")
    @EventLog(EventType.DATA_CLEAR_WEB_SIDE_MESSAGE)
    public Integer clearWebSideMessage(@RequestBody DataClearRequest request) {
        this.validParams(request);
        return dataClearService.clearWebSideMessage(request);
    }

    /**
     * 清理 操作日志
     */
    @RequestMapping("/event-log")
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
