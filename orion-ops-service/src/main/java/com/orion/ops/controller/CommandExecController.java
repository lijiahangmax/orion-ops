package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.CommandExecRequest;
import com.orion.ops.entity.vo.CommandExecVO;
import com.orion.ops.service.api.CommandExecService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    public HttpWrapper<?> submitTask(@RequestBody CommandExecRequest request) {
        Valid.notBlank(request.getCommand());
        Valid.notEmpty(request.getMachineIdList());
        request.setRelId(null);
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
     * 停止任务
     */
    @RequestMapping("/terminated")
    public Integer terminated(@RequestBody CommandExecRequest request) {
        Long id = Valid.notNull(request.getId());
        return commandExecService.terminatedExec(id);
    }

    // tail

}
