package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.UUIds;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.consts.tail.FileTailType;
import com.orion.ops.dao.FileTailListDAO;
import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.dto.FileTailDTO;
import com.orion.ops.entity.request.FileTailRequest;
import com.orion.ops.entity.vo.FileTailConfigVO;
import com.orion.ops.entity.vo.FileTailVO;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 文件tail 实现
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
    private FileTailListDAO fileTailListDAO;

    @Resource
    private ReleaseInfoService releaseInfoService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public HttpWrapper<FileTailVO> getTailToken(FileTailRequest request) {
        Long relId = request.getRelId();
        Valid.notNull(relId);
        FileTailVO res = null;
        String path = null;
        // 获取日志路径
        switch (FileTailType.of(request.getType())) {
            case EXEC_LOG:
                // 执行日志
                path = commandExecService.getExecLogFilePath(relId);
                break;
            case TAIL_LIST:
                // tail list
                res = this.getTailDetail(relId);
                Valid.notBlank(res.getMachineHost(), MessageConst.INVALID_MACHINE);
                // 更新修改时间
                this.updateFileUpdateTime(relId);
                break;
            case RELEASE_HOST:
                // 上线单 宿主机步骤
                path = releaseInfoService.getReleaseHostLogPath(relId);
                break;
            case RELEASE_STAGE:
                // 上线单 目标机器步骤
                path = releaseInfoService.getReleaseStageLogPath(relId);
                break;
            default:
                break;
        }
        if (res == null) {
            // 检查文件是否存在
            if (path == null || !Files1.isFile(path)) {
                return HttpWrapper.of(ResultCode.FILE_MISSING);
            }
            // 查询机器
            MachineInfoDO machine = machineInfoService.selectById(Const.HOST_MACHINE_ID);
            if (machine == null) {
                return HttpWrapper.error(MessageConst.INVALID_MACHINE);
            }
            // 设置返回
            res = new FileTailVO();
            res.setMachineId(machine.getId());
            res.setMachineName(machine.getMachineName());
            res.setMachineHost(machine.getMachineHost());
            res.setPath(path);
            res.setOffset(machineEnvService.getTailOffset(Const.HOST_MACHINE_ID));
            res.setCharset(machineEnvService.getTailCharset(Const.HOST_MACHINE_ID));
        }
        String token = UUIds.random19();
        res.setToken(token);

        // 设置缓存
        FileTailDTO tail = Converts.to(res, FileTailDTO.class);
        tail.setUserId(Currents.getUserId());
        tail.setMode(machineEnvService.getMachineTailMode(Const.HOST_MACHINE_ID));
        String key = Strings.format(KeyConst.FILE_TAIL_ACCESS_TOKEN, token);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(tail), KeyConst.FILE_TAIL_ACCESS_EXPIRE, TimeUnit.SECONDS);
        return HttpWrapper.<FileTailVO>ok().data(res);
    }

    @Override
    public Long insertTailFile(FileTailRequest request) {
        FileTailListDO insert = new FileTailListDO();
        insert.setAliasName(request.getName());
        insert.setMachineId(request.getMachineId());
        insert.setFilePath(request.getPath());
        insert.setFileCharset(request.getCharset());
        insert.setFileOffset(request.getOffset());
        fileTailListDAO.insert(insert);
        return insert.getId();
    }

    @Override
    public Integer updateTailFile(FileTailRequest request) {
        FileTailListDO update = new FileTailListDO();
        update.setId(request.getId());
        update.setAliasName(request.getName());
        update.setMachineId(request.getMachineId());
        update.setFilePath(request.getPath());
        update.setFileCharset(request.getCharset());
        update.setFileOffset(request.getOffset());
        update.setUpdateTime(new Date());
        return fileTailListDAO.updateById(update);
    }

    @Override
    public DataGrid<FileTailVO> tailFileList(FileTailRequest request) {
        LambdaQueryWrapper<FileTailListDO> wrapper = new LambdaQueryWrapper<FileTailListDO>()
                .eq(Objects.nonNull(request.getMachineId()), FileTailListDO::getMachineId, request.getMachineId())
                .like(!Strings.isEmpty(request.getName()), FileTailListDO::getAliasName, request.getName())
                .like(!Strings.isEmpty(request.getPath()), FileTailListDO::getFilePath, request.getPath())
                .orderByDesc(FileTailListDO::getUpdateTime);
        DataGrid<FileTailVO> dataGrid = DataQuery.of(fileTailListDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(FileTailVO.class);
        // 设置机器信息
        dataGrid.forEach(p -> {
            MachineInfoDO machine = machineInfoService.selectById(p.getMachineId());
            if (machine != null) {
                p.setMachineName(machine.getMachineName());
                p.setMachineHost(machine.getMachineHost());
            }
        });
        return dataGrid;
    }

    @Override
    public FileTailVO getTailDetail(Long id) {
        FileTailListDO fileTail = fileTailListDAO.selectById(id);
        Valid.notNull(fileTail, MessageConst.UNKNOWN_DATA);
        FileTailVO vo = Converts.to(fileTail, FileTailVO.class);
        MachineInfoDO machine = machineInfoService.selectById(fileTail.getMachineId());
        if (machine != null) {
            vo.setMachineName(machine.getMachineName());
            vo.setMachineHost(machine.getMachineHost());
        }
        return vo;
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
        return config;
    }

}
