package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.command.ExecStatus;
import com.orion.ops.consts.command.ExecType;
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
    private ExecSessionHolder execSessionHolder;

    @Override
    public HttpWrapper<Map<Long, Long>> submit(CommandExecRequest request) {
        String command = Valid.notBlank(request.getCommand());
        List<Long> machineIdList = Valid.notEmpty(request.getMachineIdList());
        // 检查是否有运行中的任务
        Long userId = Currents.getUserId();
        for (Long mid : machineIdList) {
            MachineInfoDO machine = machineInfoService.selectById(mid);
            Valid.notNull(machine, MessageConst.INVALID_MACHINE);
            LambdaQueryWrapper<CommandExecDO> wrapper = new LambdaQueryWrapper<CommandExecDO>()
                    .eq(CommandExecDO::getExecType, ExecType.BATCH_EXEC.getType())
                    .eq(CommandExecDO::getUserId, userId)
                    .eq(CommandExecDO::getMachineId, mid)
                    .in(CommandExecDO::getExecStatus, ExecStatus.WAITING.getStatus(), ExecStatus.RUNNABLE.getStatus());
            Integer task = commandExecDAO.selectCount(wrapper);
            if (task.compareTo(0) > 0) {
                return HttpWrapper.error(machine.getMachineHost() + " 有正在执行的任务");
            }
        }
        // 建立连接
        Map<Long, SessionStore> sessionStore = new LinkedHashMap<>();
        for (Long mid : machineIdList) {
            try {
                SessionStore session = machineInfoService.openSessionStore(mid);
                sessionStore.put(mid, session);
            } catch (Exception e) {
                e.printStackTrace();
                sessionStore.values().forEach(Streams::close);
                throw e;
            }
        }
        // 执行命令
        Map<Long, Long> tailToken = this.executeBatchCommand(request, sessionStore);
        return HttpWrapper.ok(tailToken);
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
                .like(Strings.isNotBlank(request.getDescription()), CommandExecDO::getDescription, request.getDescription())
                .in(Lists.isNotEmpty(request.getMachineIdList()), CommandExecDO::getMachineId, request.getMachineIdList());
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
        Valid.notNull(execDO, MessageConst.EXEC_TASK_MISSING);
        CommandExecVO execVO = Converts.to(execDO, CommandExecVO.class);
        this.assembleExecData(Collections.singletonList(execVO));
        return execVO;
    }

    @Override
    public Integer terminatedExec(Long id) {
        CommandExecDO execDO = this.selectById(id);
        Valid.notNull(execDO, MessageConst.EXEC_TASK_MISSING);
        int effect = 0;
        if (execDO.getExecStatus().equals(ExecStatus.WAITING.getStatus())
                || execDO.getExecStatus().equals(ExecStatus.RUNNABLE.getStatus())) {
            // 停止任务
            Optional.ofNullable(execSessionHolder.getHolder().get(id)).ifPresent(IExecHandler::close);
            execDO = this.selectById(id);
        }
        // 停止任务再次查询
        if (execDO.getExecStatus().equals(ExecStatus.WAITING.getStatus())
                || execDO.getExecStatus().equals(ExecStatus.RUNNABLE.getStatus())) {
            // 更新状态
            CommandExecDO updateStatus = new CommandExecDO();
            updateStatus.setId(id);
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
                    .eq(CommandExecDO::getId, id);
            return commandExecDAO.selectOne(wrapper);
        }
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
     * @param request      request
     * @param sessionStore sessions
     * @return key: mid, value: execId
     */
    private Map<Long, Long> executeBatchCommand(CommandExecRequest request, Map<Long, SessionStore> sessionStore) {
        Map<Long, Long> execResult = new LinkedHashMap<>();
        sessionStore.forEach((k, v) -> {
            ExecHint hint = new ExecHint();
            hint.setExecType(ExecType.BATCH_EXEC);
            hint.setMachineId(k);
            hint.setSession(v);
            hint.setCommand(request.getCommand());
            hint.setDescription(request.getDescription());
            IExecHandler handler = IExecHandler.with(hint);
            Long eid = handler.submit(hint);
            execResult.put(k, eid);
        });
        return execResult;
    }

}
