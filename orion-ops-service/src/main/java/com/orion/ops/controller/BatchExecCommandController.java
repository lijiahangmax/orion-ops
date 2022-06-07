package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.CommandExecRequest;
import com.orion.ops.entity.vo.CommandExecStatusVO;
import com.orion.ops.entity.vo.CommandExecVO;
import com.orion.ops.entity.vo.sftp.CommandTaskSubmitVO;
import com.orion.ops.service.api.CommandExecService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 批量执行 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 17:44
 */
@Api(tags = "批量执行")
@RestController
@RestWrapper
@RequestMapping("/orion/api/batch-exec")
public class BatchExecCommandController {

    @Resource
    private CommandExecService commandExecService;

    @PostMapping("/submit")
    @ApiOperation(value = "提交批量执行任务")
    @EventLog(EventType.EXEC_SUBMIT)
    public List<CommandTaskSubmitVO> submitTask(@RequestBody CommandExecRequest request) {
        Valid.notBlank(request.getCommand());
        Valid.notEmpty(request.getMachineIdList());
        return commandExecService.batchSubmitTask(request);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取执行列表")
    public DataGrid<CommandExecVO> list(@RequestBody CommandExecRequest request) {
        return commandExecService.execList(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取执行详情")
    public CommandExecVO detail(@RequestBody CommandExecRequest request) {
        Long id = Valid.notNull(request.getId());
        return commandExecService.execDetail(id);
    }

    @PostMapping("/write")
    @ApiOperation(value = "写入命令")
    public void write(@RequestBody CommandExecRequest request) {
        Long id = Valid.notNull(request.getId());
        String command = Valid.notEmpty(request.getCommand());
        commandExecService.writeCommand(id, command);
    }

    @PostMapping("/terminate")
    @ApiOperation(value = "停止执行任务")
    @EventLog(EventType.EXEC_TERMINATE)
    public HttpWrapper<?> terminate(@RequestBody CommandExecRequest request) {
        Long id = Valid.notNull(request.getId());
        commandExecService.terminateExec(id);
        return HttpWrapper.ok();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除任务")
    @EventLog(EventType.EXEC_DELETE)
    public Integer delete(@RequestBody CommandExecRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return commandExecService.deleteTask(idList);
    }

    @IgnoreLog
    @PostMapping("/list-status")
    @ApiOperation(value = "获取状态列表")
    public List<CommandExecStatusVO> getListStatus(@RequestBody CommandExecRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return commandExecService.getExecStatusList(idList);
    }

}
