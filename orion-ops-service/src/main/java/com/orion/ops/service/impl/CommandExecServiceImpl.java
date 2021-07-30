package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.EnvConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.command.ExecStatus;
import com.orion.ops.consts.command.ExecType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.CommandExecDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.CommandExecRequest;
import com.orion.ops.entity.vo.CommandExecVO;
import com.orion.ops.handler.exec.ExecHint;
import com.orion.ops.handler.exec.ExecSessionHolder;
import com.orion.ops.handler.exec.IExecHandler;
import com.orion.ops.service.api.CommandExecService;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.remote.channel.SessionStore;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Streams;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
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
    private ExecSessionHolder execSessionHolder;

    @Override
    public HttpWrapper<Map<String, Long>> batchSubmitTask(CommandExecRequest request) {
        Valid.notBlank(request.getCommand());
        List<Long> machineIdList = request.getMachineIdList();
        Map<Long, MachineInfoDO> machineCache = Maps.newMap();
        // 检查是否有运行中的任务
        Long userId = Currents.getUserId();
        for (Long mid : machineIdList) {
            MachineInfoDO machine = machineInfoService.selectById(mid);
            Valid.notNull(machine, MessageConst.INVALID_MACHINE);
            machineCache.put(machine.getId(), machine);
            LambdaQueryWrapper<CommandExecDO> wrapper = new LambdaQueryWrapper<CommandExecDO>()
                    .eq(CommandExecDO::getExecType, ExecType.BATCH_EXEC.getType())
                    .eq(CommandExecDO::getUserId, userId)
                    .eq(CommandExecDO::getMachineId, mid)
                    .in(CommandExecDO::getExecStatus, ExecStatus.WAITING.getStatus(), ExecStatus.RUNNABLE.getStatus());
            Integer task = commandExecDAO.selectCount(wrapper);
            if (task.compareTo(0) > 0) {
                return HttpWrapper.error(machine.getMachineHost() + " " + MessageConst.EXEC_TASK_RUNNABLE_PRESENT);
            }
        }
        // 建立连接
        Map<Long, SessionStore> sessionStoreMap = Maps.newLinkedMap();
        for (Long mid : machineIdList) {
            try {
                SessionStore session = machineInfoService.openSessionStore(mid);
                sessionStoreMap.put(mid, session);
            } catch (Exception e) {
                e.printStackTrace();
                sessionStoreMap.values().forEach(Streams::close);
                throw e;
            }
        }
        // 执行命令
        Map<Long, Long> tailToken = this.executeBatchCommand(request, sessionStoreMap, machineCache);
        Map<String, Long> result = Maps.newLinkedMap();
        tailToken.forEach((k, v) -> {
            result.put(k + "", v);
        });
        return HttpWrapper.ok(result);
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
                .eq(Objects.nonNull(request.getRelId()), CommandExecDO::getRelId, request.getRelId())
                .eq(Objects.nonNull(request.getUserId()), CommandExecDO::getUserId, request.getUserId())
                .eq(Objects.nonNull(request.getStatus()), CommandExecDO::getExecStatus, request.getStatus())
                .eq(Objects.nonNull(request.getType()), CommandExecDO::getExecType, request.getType())
                .eq(Objects.nonNull(request.getExitCode()), CommandExecDO::getExitCode, request.getExitCode())
                .eq(Objects.nonNull(request.getMachineId()), CommandExecDO::getMachineId, request.getMachineId())
                .like(Strings.isNotBlank(request.getDescription()), CommandExecDO::getDescription, request.getDescription())
                .in(Lists.isNotEmpty(request.getMachineIdList()), CommandExecDO::getMachineId, request.getMachineIdList())
                .orderByDesc(CommandExecDO::getId);
        DataGrid<CommandExecVO> dataGrid = DataQuery.of(commandExecDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(CommandExecVO.class);
        if (!dataGrid.isEmpty()) {
            this.assembleExecData(dataGrid.getRows());
        }
        return dataGrid;
    }

    @Override
    public CommandExecVO execDetail(Long id) {
        CommandExecDO execDO = this.selectById(id);
        Valid.notNull(execDO, MessageConst.EXEC_TASK_ABSENT);
        CommandExecVO execVO = Converts.to(execDO, CommandExecVO.class);
        this.assembleExecData(Collections.singletonList(execVO));
        return execVO;
    }

    @Override
    public Integer terminatedExec(Long id) {
        CommandExecDO execDO = this.selectById(id);
        Valid.notNull(execDO, MessageConst.EXEC_TASK_ABSENT);
        int effect = 0;
        if (execDO.getExecStatus().equals(ExecStatus.WAITING.getStatus())
                || execDO.getExecStatus().equals(ExecStatus.RUNNABLE.getStatus())) {
            // 停止任务
            Optional.ofNullable(execSessionHolder.getSession(id)).ifPresent(IExecHandler::close);
            execDO = this.selectById(id);
        }
        // 停止任务再次查询
        if (execDO.getExecStatus().equals(ExecStatus.WAITING.getStatus())
                || execDO.getExecStatus().equals(ExecStatus.RUNNABLE.getStatus())) {
            // 更新状态
            CommandExecDO updateStatus = new CommandExecDO();
            updateStatus.setId(id);
            updateStatus.setExitCode(-1);
            updateStatus.setExecStatus(ExecStatus.TERMINATED.getStatus());
            effect += commandExecDAO.updateById(updateStatus);
        }
        return effect;
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
                .map(s -> MachineEnvAttr.LOG_PATH.getValue() + s)
                .orElse(null);
    }

    /**
     * 填充组装数据
     *
     * @param execList execList
     */
    private void assembleExecData(List<CommandExecVO> execList) {
        Map<Long, MachineInfoDO> machineCache = Maps.newMap();
        for (CommandExecVO exec : execList) {
            Optional.ofNullable(machineCache.computeIfAbsent(exec.getMachineId(), machineInfoDAO::selectById))
                    .ifPresent(m -> exec.setMachineHost(m.getMachineHost()));
        }
    }

    /**
     * 执行命令
     *
     * @param request         request
     * @param sessionStoreMap sessions
     * @param machineCache    machineCache
     * @return key: mid, value: execId
     */
    private Map<Long, Long> executeBatchCommand(CommandExecRequest request, Map<Long, SessionStore> sessionStoreMap, Map<Long, MachineInfoDO> machineCache) {
        Map<Long, Long> execResult = Maps.newLinkedMap();
        sessionStoreMap.forEach((machineId, session) -> {
            ExecHint hint = new ExecHint();
            hint.setExecType(ExecType.BATCH_EXEC);
            hint.setRelId(request.getRelId());
            hint.setMachineId(machineId);
            hint.setSession(session);
            hint.setDescription(request.getDescription());
            // 设置命令
            MachineInfoDO machine = machineCache.get(machineId);
            hint.setMachine(machine);
            hint.setCommand(this.replaceCommand(machine, request.getCommand()));
            // 提交执行
            IExecHandler handler = IExecHandler.with(hint);
            Long eid = handler.submit(hint);
            execResult.put(machineId, eid);
        });
        return execResult;
    }

    /**
     * 替换命令占位符
     *
     * @param machine machine
     * @param command command
     * @return replaced command
     */
    private String replaceCommand(MachineInfoDO machine, String command) {
        Map<String, String> env = machineEnvService.getFullMachineEnv(machine.getId(), EnvConst.ENV_PREFIX);
        return Strings.format(command, EnvConst.SYMBOL, env);
    }

}
