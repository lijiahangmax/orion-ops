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
package cn.orionsec.ops.service.impl;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.command.ExecStatus;
import cn.orionsec.ops.constant.command.ExecType;
import cn.orionsec.ops.constant.env.EnvConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.dao.CommandExecDAO;
import cn.orionsec.ops.dao.MachineInfoDAO;
import cn.orionsec.ops.entity.domain.CommandExecDO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.entity.request.exec.CommandExecRequest;
import cn.orionsec.ops.entity.vo.exec.CommandExecStatusVO;
import cn.orionsec.ops.entity.vo.exec.CommandExecVO;
import cn.orionsec.ops.entity.vo.exec.CommandTaskSubmitVO;
import cn.orionsec.ops.handler.exec.ExecSessionHolder;
import cn.orionsec.ops.handler.exec.IExecHandler;
import cn.orionsec.ops.service.api.CommandExecService;
import cn.orionsec.ops.service.api.MachineEnvService;
import cn.orionsec.ops.service.api.MachineInfoService;
import cn.orionsec.ops.service.api.SystemEnvService;
import cn.orionsec.ops.utils.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.io.Files1;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 命令执行服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 18:00
 */
@Service("commandExecService")
public class CommandExecServiceImpl implements CommandExecService {

    @Resource
    private CommandExecDAO commandExecDAO;

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private SystemEnvService systemEnvService;

    @Resource
    private ExecSessionHolder execSessionHolder;

    @Override
    public List<CommandTaskSubmitVO> batchSubmitTask(CommandExecRequest request) {
        UserDTO user = Currents.getUser();
        List<Long> machineIdList = request.getMachineIdList();
        // 查询机器信息
        Map<Long, MachineInfoDO> machineStore = Maps.newMap();
        for (Long mid : machineIdList) {
            MachineInfoDO machine = machineInfoService.selectById(mid);
            Valid.notNull(machine, MessageConst.INVALID_MACHINE);
            machineStore.put(machine.getId(), machine);
        }
        // 设置命令
        String command = request.getCommand();
        final boolean containsEnv = command.contains(EnvConst.SYMBOL);
        if (containsEnv) {
            // 查询系统环境变量
            Map<String, String> systemEnv = systemEnvService.getFullSystemEnv();
            command = Strings.format(command, EnvConst.SYMBOL, systemEnv);
        }
        // 批量执行命令
        List<CommandTaskSubmitVO> list = Lists.newList();
        for (Long mid : machineIdList) {
            MachineInfoDO machine = machineStore.get(mid);
            // 插入执行命令
            CommandExecDO record = new CommandExecDO();
            record.setUserId(user.getId());
            record.setUserName(user.getUsername());
            record.setMachineId(mid);
            record.setMachineName(machine.getMachineName());
            record.setMachineHost(machine.getMachineHost());
            record.setMachineTag(machine.getMachineTag());
            record.setExecType(ExecType.BATCH_EXEC.getType());
            record.setExecStatus(ExecStatus.WAITING.getStatus());
            record.setDescription(request.getDescription());
            if (containsEnv) {
                // 查询机器环境变量
                Map<String, String> machineEnv = machineEnvService.getFullMachineEnv(mid);
                record.setExecCommand(Strings.format(command, EnvConst.SYMBOL, machineEnv));
            } else {
                record.setExecCommand(command);
            }
            commandExecDAO.insert(record);
            // 设置日志路径
            Long execId = record.getId();
            String logPath = PathBuilders.getExecLogPath(Const.COMMAND_DIR, execId, machine.getId());
            CommandExecDO update = new CommandExecDO();
            update.setId(execId);
            update.setLogPath(logPath);
            record.setLogPath(logPath);
            commandExecDAO.updateById(update);
            // 提交执行任务
            IExecHandler.with(execId).exec();
            // 返回
            CommandTaskSubmitVO submitVO = new CommandTaskSubmitVO();
            submitVO.setExecId(execId);
            submitVO.setMachineId(mid);
            submitVO.setMachineName(machine.getMachineName());
            submitVO.setMachineHost(machine.getMachineHost());
            list.add(submitVO);
        }
        // 设置日志参数
        List<Long> idList = list.stream()
                .map(CommandTaskSubmitVO::getExecId)
                .collect(Collectors.toList());
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, list.size());
        return list;
    }

    @Override
    public DataGrid<CommandExecVO> execList(CommandExecRequest request) {
        if (!Currents.isAdministrator()) {
            request.setUserId(Currents.getUserId());
        }
        if (!Strings.isBlank(request.getHost())) {
            List<Long> machineIds = machineInfoDAO.selectIdByHost(request.getHost());
            if (machineIds.isEmpty()) {
                return new DataGrid<>();
            }
            request.setMachineIdList(machineIds);
        }
        LambdaQueryWrapper<CommandExecDO> wrapper = new LambdaQueryWrapper<CommandExecDO>()
                .eq(Objects.nonNull(request.getId()), CommandExecDO::getId, request.getId())
                .eq(Objects.nonNull(request.getUserId()), CommandExecDO::getUserId, request.getUserId())
                .eq(Objects.nonNull(request.getStatus()), CommandExecDO::getExecStatus, request.getStatus())
                .eq(Objects.nonNull(request.getType()), CommandExecDO::getExecType, request.getType())
                .eq(Objects.nonNull(request.getExitCode()), CommandExecDO::getExitCode, request.getExitCode())
                .eq(Objects.nonNull(request.getMachineId()), CommandExecDO::getMachineId, request.getMachineId())
                .like(Strings.isNotBlank(request.getMachineName()), CommandExecDO::getMachineName, request.getMachineName())
                .like(Strings.isNotBlank(request.getCommand()), CommandExecDO::getExecCommand, request.getCommand())
                .like(Strings.isNotBlank(request.getUsername()), CommandExecDO::getUserName, request.getUsername())
                .like(Strings.isNotBlank(request.getDescription()), CommandExecDO::getDescription, request.getDescription())
                .in(Lists.isNotEmpty(request.getMachineIdList()), CommandExecDO::getMachineId, request.getMachineIdList())
                .orderByDesc(CommandExecDO::getId);
        DataGrid<CommandExecVO> dataGrid = DataQuery.of(commandExecDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(CommandExecVO.class);
        if (!dataGrid.isEmpty()) {
            this.assembleExecData(dataGrid.getRows(), request.isOmitCommand());
        }
        return dataGrid;
    }

    @Override
    public CommandExecVO execDetail(Long id) {
        CommandExecDO execDO = this.selectById(id);
        Valid.notNull(execDO, MessageConst.EXEC_TASK_ABSENT);
        CommandExecVO execVO = Converts.to(execDO, CommandExecVO.class);
        this.assembleExecData(Collections.singletonList(execVO), false);
        return execVO;
    }

    @Override
    public void writeCommand(Long id, String command) {
        CommandExecDO execDO = this.selectById(id);
        Valid.notNull(execDO, MessageConst.EXEC_TASK_ABSENT);
        Valid.isTrue(ExecStatus.RUNNABLE.getStatus().equals(execDO.getExecStatus()), MessageConst.ILLEGAL_STATUS);
        // 获取任务信息
        IExecHandler session = execSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.EXEC_TASK_THREAD_ABSENT);
        session.write(command);
    }

    @Override
    public void terminateExec(Long id) {
        CommandExecDO execDO = this.selectById(id);
        Valid.notNull(execDO, MessageConst.EXEC_TASK_ABSENT);
        Valid.isTrue(ExecStatus.RUNNABLE.getStatus().equals(execDO.getExecStatus()), MessageConst.ILLEGAL_STATUS);
        // 获取任务并停止
        IExecHandler session = execSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        session.terminate();
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
    }

    @Override
    public Integer deleteTask(List<Long> idList) {
        List<CommandExecDO> execList = commandExecDAO.selectBatchIds(idList);
        Valid.notEmpty(execList, MessageConst.EXEC_TASK_ABSENT);
        // 检查是否可删除
        boolean canDelete = execList.stream()
                .map(CommandExecDO::getExecStatus)
                .noneMatch(s -> ExecStatus.WAITING.getStatus().equals(s) || ExecStatus.RUNNABLE.getStatus().equals(s));
        Valid.isTrue(canDelete, MessageConst.ILLEGAL_STATUS);
        int effect = commandExecDAO.deleteBatchIds(idList);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, idList.size());
        return effect;
    }

    @Override
    public List<CommandExecStatusVO> getExecStatusList(List<Long> execIdList) {
        return execIdList.stream()
                .map(commandExecDAO::selectStatusById)
                .filter(Objects::nonNull)
                .map(s -> Converts.to(s, CommandExecStatusVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CommandExecDO selectById(Long id) {
        if (Currents.isAdministrator()) {
            return commandExecDAO.selectById(id);
        } else {
            Wrapper<CommandExecDO> wrapper = new LambdaQueryWrapper<CommandExecDO>()
                    .eq(CommandExecDO::getUserId, Currents.getUserId())
                    .eq(CommandExecDO::getId, id)
                    .last(Const.LIMIT_1);
            return commandExecDAO.selectOne(wrapper);
        }
    }

    @Override
    public String getExecLogFilePath(Long id) {
        LambdaQueryWrapper<CommandExecDO> wrapper = new LambdaQueryWrapper<CommandExecDO>()
                .eq(!Currents.isAdministrator(), CommandExecDO::getUserId, Currents.getUserId())
                .eq(CommandExecDO::getId, id);
        return Optional.ofNullable(commandExecDAO.selectOne(wrapper))
                .map(CommandExecDO::getLogPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

    /**
     * 填充组装数据
     *
     * @param execList    execList
     * @param omitCommand 省略命令
     */
    private void assembleExecData(List<CommandExecVO> execList, boolean omitCommand) {
        for (CommandExecVO exec : execList) {
            if (omitCommand) {
                // 命令省略
                exec.setCommand(Strings.omit(exec.getCommand(), Const.EXEC_COMMAND_OMIT));
            }
        }
    }

}
