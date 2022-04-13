package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.TimedType;
import com.orion.ops.entity.request.ApplicationPipelineDetailRecordRequest;
import com.orion.ops.entity.request.ApplicationPipelineRecordRequest;
import com.orion.ops.entity.vo.ApplicationPipelineDetailRecordVO;
import com.orion.ops.entity.vo.ApplicationPipelineRecordListVO;
import com.orion.ops.service.api.ApplicationPipelineDetailRecordService;
import com.orion.ops.service.api.ApplicationPipelineRecordService;
import com.orion.ops.task.TaskRegister;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 应用流水线明细 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:54
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-pipeline-record")
public class ApplicationPipelineRecordController {

    @Resource
    private ApplicationPipelineRecordService applicationPipelineRecordService;

    @Resource
    private ApplicationPipelineDetailRecordService applicationPipelineDetailRecordService;

    @Resource
    private TaskRegister taskRegister;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<ApplicationPipelineRecordListVO> getPipelineRecordList(@RequestBody ApplicationPipelineRecordRequest request) {
        Valid.notNull(request.getProfileId());
        return applicationPipelineRecordService.getPipelineRecordList(request);
    }

    /**
     * 明细详情
     */
    @RequestMapping("/record-details")
    public List<ApplicationPipelineDetailRecordVO> getRecordDetails(@RequestBody ApplicationPipelineRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineDetailRecordService.getRecordDetails(id);
    }

    /**
     * 提交
     */
    @RequestMapping("/submit")
    public Long submitPipelineExec(@RequestBody ApplicationPipelineRecordRequest request) {
        Valid.notNull(request.getPipelineId());
        Valid.notBlank(request.getTitle());
        List<ApplicationPipelineDetailRecordRequest> details = Valid.notEmpty(request.getDetails());
        for (ApplicationPipelineDetailRecordRequest detail : details) {
            Valid.notNull(detail.getId());
        }
        TimedType timedType = Valid.notNull(TimedType.of(request.getTimedExec()));
        if (TimedType.TIMED.equals(timedType)) {
            Date timedExecTime = Valid.notNull(request.getTimedExecTime());
            Valid.isTrue(timedExecTime.compareTo(new Date()) > 0, MessageConst.TIMED_GREATER_THAN_NOW);
        }
        Long id = applicationPipelineRecordService.submitPipelineExec(request);
        // 提交任务
        // if (PipelineStatus.WAIT_SCHEDULE.getStatus().equals(request.getStatus())) {
        //     taskRegister.submit(TaskType.PIPELINE, request.getTimedExecTime(), id);
        // }
        return id;
    }

    // 执行

    // 复制

    // 详情

    // 操作详情

    // 设置定时

    // 取消定时

    // 审核

    // 删除

    // 状态

    // 停止

    // 部分停止

    // 跳过

    // 注入站内信

    // 注入操作日志

}
