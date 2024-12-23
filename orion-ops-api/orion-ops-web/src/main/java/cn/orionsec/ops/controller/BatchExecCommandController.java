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
package cn.orionsec.ops.controller;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.IgnoreLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.exec.CommandExecRequest;
import cn.orionsec.ops.entity.vo.exec.CommandExecStatusVO;
import cn.orionsec.ops.entity.vo.exec.CommandExecVO;
import cn.orionsec.ops.entity.vo.exec.CommandTaskSubmitVO;
import cn.orionsec.ops.service.api.CommandExecService;
import cn.orionsec.ops.utils.Valid;
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
