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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 批量执行controller
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 17:44
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/exec")
public class CommandExecController {

    @Resource
    private CommandExecService commandExecService;

    /**
     * 提交批量执行任务
     */
    @RequestMapping("/submit")
    @EventLog(EventType.EXEC_SUBMIT)
    public List<CommandTaskSubmitVO> submitTask(@RequestBody CommandExecRequest request) {
        Valid.notBlank(request.getCommand());
        Valid.notEmpty(request.getMachineIdList());
        return commandExecService.batchSubmitTask(request);
    }

    /**
     * 查询列表
     */
    @RequestMapping("/list")
    public DataGrid<CommandExecVO> list(@RequestBody CommandExecRequest request) {
        return commandExecService.execList(request);
    }

    /**
     * 查询详情
     */
    @RequestMapping("/detail")
    public CommandExecVO detail(@RequestBody CommandExecRequest request) {
        Long id = Valid.notNull(request.getId());
        return commandExecService.execDetail(id);
    }

    /**
     * 写入命令
     */
    @RequestMapping("/write")
    public void write(@RequestBody CommandExecRequest request) {
        Long id = Valid.notNull(request.getId());
        String command = Valid.notBlank(request.getCommand());
        commandExecService.writeCommand(id, command);
    }

    /**
     * 停止任务
     */
    @RequestMapping("/terminate")
    @EventLog(EventType.EXEC_TERMINATE)
    public HttpWrapper<?> terminate(@RequestBody CommandExecRequest request) {
        Long id = Valid.notNull(request.getId());
        commandExecService.terminateExec(id);
        return HttpWrapper.ok();
    }

    /**
     * 删除任务
     */
    @RequestMapping("/delete")
    @EventLog(EventType.EXEC_DELETE)
    public Integer delete(@RequestBody CommandExecRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return commandExecService.deleteTask(idList);
    }

    /**
     * 状态列表
     */
    @IgnoreLog
    @RequestMapping("/list-status")
    public List<CommandExecStatusVO> getListStatus(@RequestBody CommandExecRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return commandExecService.getExecStatusList(idList);
    }

}
