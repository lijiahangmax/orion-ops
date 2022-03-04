package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.command.ExecStatus;
import com.orion.ops.consts.command.ExecType;
import com.orion.ops.consts.env.EnvConst;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.event.EventParamsHolder;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.CommandExecDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.CommandExecRequest;
import com.orion.ops.entity.vo.CommandExecStatusVO;
import com.orion.ops.entity.vo.CommandExecVO;
import com.orion.ops.entity.vo.sftp.CommandTaskSubmitVO;
import com.orion.ops.handler.exec.ExecSessionHolder;
import com.orion.ops.handler.exec.IExecHandler;
import com.orion.ops.service.api.CommandExecService;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.service.api.SystemEnvService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
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
        if (!execDO.getExecStatus().equals(ExecStatus.RUNNABLE.getStatus())) {
            return;
        }
        // 获取任务信息
        IExecHandler session = execSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.EXEC_TASK_THREAD_ABSENT);
        session.write(command + Const.LF);
    }

    @Override
    public void terminatedExec(Long id) {
        CommandExecDO execDO = this.selectById(id);
        Valid.notNull(execDO, MessageConst.EXEC_TASK_ABSENT);
        Valid.isTrue(ExecStatus.RUNNABLE.getStatus().equals(execDO.getExecStatus()), MessageConst.ILLEGAL_STATUS);
        // 获取任务并停止
        IExecHandler session = execSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        session.terminated();
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
