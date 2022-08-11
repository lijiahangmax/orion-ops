package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.id.UUIds;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.KeyConst;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.ResultCode;
import com.orion.ops.constant.env.EnvConst;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.constant.tail.FileTailMode;
import com.orion.ops.constant.tail.FileTailType;
import com.orion.ops.dao.FileTailListDAO;
import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.dto.file.FileTailDTO;
import com.orion.ops.entity.request.file.FileTailRequest;
import com.orion.ops.entity.vo.tail.FileTailConfigVO;
import com.orion.ops.entity.vo.tail.FileTailVO;
import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailSessionHolder;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Utils;
import com.orion.ops.utils.Valid;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 文件 tail 实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/1 23:33
 */
@Service("fileTailService")
public class FileTailServiceImpl implements FileTailService {

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private CommandExecService commandExecService;

    @Resource
    private ApplicationBuildService applicationBuildService;

    @Resource
    private ApplicationReleaseMachineService applicationReleaseMachineService;

    @Resource
    private SchedulerTaskMachineRecordService schedulerTaskMachineRecordService;

    @Resource
    private ApplicationActionLogService applicationActionLogService;

    @Resource
    private FileTailListDAO fileTailListDAO;

    @Resource
    private TailSessionHolder tailSessionHolder;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public FileTailVO getTailToken(FileTailType type, Long relId) {
        FileTailVO res;
        // 获取日志路径
        final boolean isLocal = type.isLocal();
        if (isLocal) {
            String path = this.getTailFilePath(type, relId);
            res = new FileTailVO();
            res.setPath(path);
            res.setOffset(machineEnvService.getTailOffset(Const.HOST_MACHINE_ID));
            res.setCharset(machineEnvService.getTailCharset(Const.HOST_MACHINE_ID));
            res.setCommand(machineEnvService.getTailDefaultCommand(Const.HOST_MACHINE_ID));
        } else {
            // tail list
            FileTailListDO fileTail = fileTailListDAO.selectById(relId);
            Valid.notNull(fileTail, MessageConst.UNKNOWN_DATA);
            res = Converts.to(fileTail, FileTailVO.class);
        }
        // 设置命令
        this.replaceTailCommand(res);
        // 设置机器信息
        MachineInfoDO machine = machineInfoService.selectById(isLocal ? Const.HOST_MACHINE_ID : res.getMachineId());
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        res.setMachineId(machine.getId());
        res.setMachineName(machine.getMachineName());
        res.setMachineHost(machine.getMachineHost());
        res.setMachineStatus(machine.getMachineStatus());
        // 设置token
        String token = UUIds.random19();
        res.setToken(token);
        // 设置缓存
        FileTailDTO tail = Converts.to(res, FileTailDTO.class);
        tail.setUserId(Currents.getUserId());
        // 追踪模式
        String tailMode = isLocal ? FileTailMode.getHostTailMode() : res.getTailMode();
        tail.setMode(tailMode);
        String key = Strings.format(KeyConst.FILE_TAIL_ACCESS_TOKEN, token);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(tail), KeyConst.FILE_TAIL_ACCESS_EXPIRE, TimeUnit.SECONDS);
        // 本地则不返回命令
        if (isLocal) {
            res.setCommand(null);
        }
        res.setTailMode(tailMode);
        return res;
    }

    @Override
    public Long insertTailFile(FileTailRequest request) {
        // 名称重复校验
        Long machineId = request.getMachineId();
        String name = request.getName();
        this.checkNamePresent(null, name);
        // 插入
        FileTailListDO insert = new FileTailListDO();
        insert.setAliasName(name);
        insert.setMachineId(machineId);
        insert.setFilePath(request.getPath());
        insert.setFileCharset(request.getCharset());
        insert.setFileOffset(request.getOffset());
        insert.setTailCommand(request.getCommand());
        insert.setTailMode(FileTailMode.of(request.getTailMode(), Const.HOST_MACHINE_ID.equals(machineId)).getMode());
        fileTailListDAO.insert(insert);
        // 设置日志参数
        EventParamsHolder.addParams(insert);
        return insert.getId();
    }

    @Override
    public Integer updateTailFile(FileTailRequest request) {
        // 名称重复校验
        Long id = request.getId();
        String name = request.getName();
        this.checkNamePresent(id, name);
        // 查询文件
        FileTailListDO beforeTail = fileTailListDAO.selectById(id);
        Valid.notNull(beforeTail, MessageConst.UNKNOWN_DATA);
        Long machineId = request.getMachineId();
        // 修改
        FileTailListDO update = new FileTailListDO();
        update.setId(id);
        update.setAliasName(name);
        update.setMachineId(machineId);
        update.setFilePath(request.getPath());
        update.setFileOffset(request.getOffset());
        update.setFileCharset(request.getCharset());
        update.setTailCommand(request.getCommand());
        update.setTailMode(FileTailMode.of(request.getTailMode(), Const.HOST_MACHINE_ID.equals(machineId)).getMode());
        update.setUpdateTime(new Date());
        int effect = fileTailListDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParams(update);
        EventParamsHolder.addParam(EventKeys.NAME, beforeTail.getAliasName());
        return effect;
    }

    @Override
    public void uploadTailFiles(List<FileTailRequest> files) {
        // 获取宿主机配置
        FileTailConfigVO config = this.getMachineConfig(Const.HOST_MACHINE_ID);
        // 设置插入数据
        List<FileTailListDO> insertFiles = Lists.newList();
        for (FileTailRequest file : files) {
            // 名称重复校验
            String name = file.getName();
            if (name.length() > 32) {
                name = name.substring(0, 32);
            }
            try {
                this.checkNamePresent(Const.HOST_MACHINE_ID, name);
            } catch (Exception e) {
                name = Utils.getRandomSuffix() + Const.DASHED + name;
            }
            // 插入
            FileTailListDO insert = new FileTailListDO();
            insert.setAliasName(name);
            insert.setMachineId(Const.HOST_MACHINE_ID);
            insert.setFilePath(file.getPath());
            insert.setFileCharset(config.getCharset());
            insert.setFileOffset(config.getOffset());
            insert.setTailCommand(config.getCommand());
            insert.setTailMode(FileTailMode.TRACKER.getMode());
            insertFiles.add(insert);
        }
        insertFiles.forEach(fileTailListDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParams(insertFiles);
        EventParamsHolder.addParam(EventKeys.COUNT, files.size());
    }

    @Override
    public Integer deleteTailFile(List<Long> idList) {
        // 删除
        int effect = fileTailListDAO.deleteBatchIds(idList);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, effect);
        return effect;
    }

    @Override
    public Integer deleteByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<FileTailListDO> wrapper = new LambdaQueryWrapper<FileTailListDO>()
                .in(FileTailListDO::getMachineId, machineIdList);
        return fileTailListDAO.delete(wrapper);
    }

    @Override
    public DataGrid<FileTailVO> tailFileList(FileTailRequest request) {
        LambdaQueryWrapper<FileTailListDO> wrapper = new LambdaQueryWrapper<FileTailListDO>()
                .eq(Objects.nonNull(request.getMachineId()), FileTailListDO::getMachineId, request.getMachineId())
                .like(!Strings.isEmpty(request.getName()), FileTailListDO::getAliasName, request.getName())
                .like(!Strings.isEmpty(request.getPath()), FileTailListDO::getFilePath, request.getPath())
                .like(!Strings.isEmpty(request.getCommand()), FileTailListDO::getTailCommand, request.getCommand())
                .orderByDesc(FileTailListDO::getUpdateTime);
        DataGrid<FileTailVO> dataGrid = DataQuery.of(fileTailListDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(FileTailVO.class);
        // 设置机器信息
        Map<Long, MachineInfoDO> machineCache = Maps.newMap();
        dataGrid.forEach(p -> {
            MachineInfoDO machine = machineCache.computeIfAbsent(p.getMachineId(),
                    mid -> machineInfoService.selectById(p.getMachineId()));
            if (machine != null) {
                p.setMachineName(machine.getMachineName());
                p.setMachineHost(machine.getMachineHost());
                p.setMachineStatus(machine.getMachineStatus());
            }
        });
        return dataGrid;
    }

    @Override
    public FileTailVO tailFileDetail(Long id) {
        FileTailListDO tail = fileTailListDAO.selectById(id);
        Valid.notNull(tail, MessageConst.UNKNOWN_DATA);
        FileTailVO vo = Converts.to(tail, FileTailVO.class);
        // 设置机器信息
        MachineInfoDO machine = machineInfoService.selectById(tail.getMachineId());
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        vo.setMachineName(machine.getMachineName());
        vo.setMachineHost(machine.getMachineHost());
        vo.setMachineStatus(machine.getMachineStatus());
        return vo;
    }

    /**
     * 获取本机 tail 路径
     *
     * @param type  type
     * @param relId relId
     * @return path
     */
    private String getTailFilePath(FileTailType type, Long relId) {
        String path;
        switch (type) {
            case EXEC_LOG:
                // 执行日志
                path = commandExecService.getExecLogFilePath(relId);
                break;
            case APP_BUILD_LOG:
                // 应用构建日志
                path = applicationBuildService.getBuildLogPath(relId);
                break;
            case APP_RELEASE_LOG:
                // 应用发布日志
                path = applicationReleaseMachineService.getReleaseMachineLogPath(relId);
                break;
            case SCHEDULER_TASK_MACHINE_LOG:
                // 调度任务机器日志
                path = schedulerTaskMachineRecordService.getTaskMachineLogPath(relId);
                break;
            case APP_ACTION_LOG:
                // 应用操作日志
                path = applicationActionLogService.getActionLogPath(relId);
                break;
            default:
                path = null;
        }
        // 检查文件是否存在
        File file;
        if (path == null || !Files1.isFile(file = new File(path))) {
            throw Exceptions.httpWrapper(HttpWrapper.of(ResultCode.FILE_MISSING));
        }
        return file.getAbsolutePath();
    }

    /**
     * 替换 tail 命令
     *
     * @param res res
     */
    private void replaceTailCommand(FileTailVO res) {
        Map<String, Object> env = Maps.newMap(4);
        env.put(EnvConst.FILE, Files1.getPath(res.getPath()));
        env.put(EnvConst.OFFSET, res.getOffset());
        res.setCommand(Strings.format(res.getCommand(), EnvConst.SYMBOL, env));
    }

    @Override
    public Integer updateFileUpdateTime(Long id) {
        FileTailListDO update = new FileTailListDO();
        update.setId(id);
        update.setUpdateTime(new Date());
        return fileTailListDAO.updateById(update);
    }

    @Override
    public FileTailConfigVO getMachineConfig(Long machineId) {
        Valid.notNull(machineInfoService.selectById(machineId), MessageConst.INVALID_MACHINE);
        FileTailConfigVO config = new FileTailConfigVO();
        // offset
        Integer offset = machineEnvService.getTailOffset(machineId);
        config.setOffset(offset);
        // charset
        String charset = machineEnvService.getTailCharset(machineId);
        config.setCharset(charset);
        // command
        config.setCommand(machineEnvService.getTailDefaultCommand(machineId));
        return config;
    }

    @Override
    public void writeCommand(String token, String command) {
        // 获取任务信息
        ITailHandler session = tailSessionHolder.getSession(token);
        Valid.notNull(session, MessageConst.EXEC_TASK_THREAD_ABSENT);
        session.write(command);
    }

    /**
     * 检查名称是否存在
     *
     * @param id   id
     * @param name name
     */
    private void checkNamePresent(Long id, String name) {
        LambdaQueryWrapper<FileTailListDO> presentWrapper = new LambdaQueryWrapper<FileTailListDO>()
                .ne(id != null, FileTailListDO::getId, id)
                .eq(FileTailListDO::getAliasName, name);
        boolean present = DataQuery.of(fileTailListDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

}
