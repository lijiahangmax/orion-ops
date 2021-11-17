package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.ReleaseActionDAO;
import com.orion.ops.dao.ReleaseBillDAO;
import com.orion.ops.dao.ReleaseMachineDAO;
import com.orion.ops.entity.domain.ReleaseActionDO;
import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.ops.entity.domain.ReleaseMachineDO;
import com.orion.ops.entity.request.ApplicationReleaseBillRequest;
import com.orion.ops.entity.vo.ReleaseActionVO;
import com.orion.ops.entity.vo.ReleaseBillDetailVO;
import com.orion.ops.entity.vo.ReleaseBillListVO;
import com.orion.ops.entity.vo.ReleaseMachineVO;
import com.orion.ops.service.api.ReleaseInfoService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.FileReaders;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 上线单信息查询service 实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 17:15
 */
@Service("releaseInfoService")
public class ReleaseInfoServiceImpl implements ReleaseInfoService {

    @Resource
    private ReleaseBillDAO releaseBillDAO;

    @Resource
    private ReleaseMachineDAO releaseMachineDAO;

    @Resource
    private ReleaseActionDAO releaseActionDAO;

    @Override
    public ReleaseBillDO getReleaseBill(Long id) {
        return releaseBillDAO.selectById(id);
    }

    @Override
    public ReleaseBillDO getLastReleaseBill(Long appId, Long profileId) {
        LambdaQueryWrapper<ReleaseBillDO> wrapper = new LambdaQueryWrapper<ReleaseBillDO>()
                .eq(ReleaseBillDO::getAppId, appId)
                .eq(ReleaseBillDO::getProfileId, profileId)
                .orderByDesc(ReleaseBillDO::getId)
                .last(Const.LIMIT_1);
        return releaseBillDAO.selectOne(wrapper);
    }

    @Override
    public List<ReleaseMachineDO> getReleaseMachine(Long releaseId) {
        LambdaQueryWrapper<ReleaseMachineDO> wrapper = new LambdaQueryWrapper<ReleaseMachineDO>()
                .eq(ReleaseMachineDO::getReleaseId, releaseId);
        return releaseMachineDAO.selectList(wrapper);
    }

    @Override
    public List<ReleaseActionDO> getReleaseAction(Long releaseId) {
        LambdaQueryWrapper<ReleaseActionDO> wrapper = new LambdaQueryWrapper<ReleaseActionDO>()
                .eq(ReleaseActionDO::getReleaseId, releaseId);
        return releaseActionDAO.selectList(wrapper);
    }

    @Override
    public List<ReleaseActionDO> getReleaseAction(Long releaseId, Long machineId) {
        LambdaQueryWrapper<ReleaseActionDO> wrapper = new LambdaQueryWrapper<ReleaseActionDO>()
                .eq(ReleaseActionDO::getReleaseId, releaseId)
                .eq(ReleaseActionDO::getMachineId, machineId);
        return releaseActionDAO.selectList(wrapper);
    }

    @Override
    public DataGrid<ReleaseBillListVO> releaseBillList(ApplicationReleaseBillRequest request) {
        Long userId = Currents.getUserId();
        LambdaQueryWrapper<ReleaseBillDO> wrapper = new LambdaQueryWrapper<ReleaseBillDO>()
                .like(!Strings.isBlank(request.getTitle()), ReleaseBillDO::getReleaseTitle, request.getTitle())
                .eq(Objects.nonNull(request.getAppId()), ReleaseBillDO::getAppId, request.getAppId())
                .eq(Objects.nonNull(request.getProfileId()), ReleaseBillDO::getProfileId, request.getProfileId())
                .eq(Objects.nonNull(request.getStatus()), ReleaseBillDO::getReleaseStatus, request.getStatus())
                .and(Const.ENABLE.equals(request.getOnlyMyself()), w -> w
                        .eq(ReleaseBillDO::getCreateUserId, userId)
                        .or()
                        .eq(ReleaseBillDO::getReleaseUserId, userId))
                .orderByDesc(ReleaseBillDO::getUpdateTime);
        return DataQuery.of(releaseBillDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(ReleaseBillListVO.class);
    }

    @Override
    public ReleaseBillDetailVO releaseBillDetail(Long id) {
        // 查询上线单信息
        ReleaseBillDO releaseBill = Valid.notNull(releaseBillDAO.selectById(id), MessageConst.RELEASE_BILL_ABSENT);
        ReleaseBillDetailVO detailVO = Converts.to(releaseBill, ReleaseBillDetailVO.class);
        // 查询主机操作信息
        List<ReleaseActionDO> hostActions = this.getReleaseAction(id, Const.HOST_MACHINE_ID);
        List<ReleaseActionVO> hostActionVO = Converts.toList(hostActions, ReleaseActionVO.class);
        for (int i = 0; i < hostActionVO.size(); i++) {
            hostActionVO.get(i).setStep(i + 1);
        }
        detailVO.setHostActions(hostActionVO);
        // 查询机器信息
        List<ReleaseMachineDO> machines = this.getReleaseMachine(id);
        List<ReleaseMachineVO> machineVO = Converts.toList(machines, ReleaseMachineVO.class);
        detailVO.setMachines(machineVO);
        // 查询机器操作信息
        for (ReleaseMachineVO releaseMachineVO : machineVO) {
            List<ReleaseActionDO> targetActions = this.getReleaseAction(id, releaseMachineVO.getMachineId());
            List<ReleaseActionVO> targetActionVO = Converts.toList(targetActions, ReleaseActionVO.class);
            for (int i = 0; i < targetActionVO.size(); i++) {
                targetActionVO.get(i).setStep(i + 1);
            }
            releaseMachineVO.setActions(targetActionVO);
        }
        return detailVO;
    }

    @Override
    public String releaseTargetLog(Long releaseId) {
        ReleaseBillDO releaseBill = releaseBillDAO.selectById(releaseId);
        Valid.notNull(releaseBill, MessageConst.RELEASE_BILL_ABSENT);
        return this.getFileTailLine(releaseBill.getLogPath());
    }

    @Override
    public String releaseMachineLog(Long releaseMachineId) {
        ReleaseMachineDO releaseMachine = releaseMachineDAO.selectById(releaseMachineId);
        Valid.notNull(releaseMachine, MessageConst.RELEASE_MACHINE_ABSENT);
        return this.getFileTailLine(releaseMachine.getLogPath());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyReleaseMachine(Long sourceReleaseId, Long targetReleaseId) {
        // 查询原始数据
        List<ReleaseMachineDO> sourceMachines = this.getReleaseMachine(sourceReleaseId);
        for (ReleaseMachineDO sourceMachine : sourceMachines) {
            // 构建数据
            ReleaseMachineDO targetMachine = new ReleaseMachineDO();
            targetMachine.setReleaseId(targetReleaseId);
            targetMachine.setMachineId(sourceMachine.getMachineId());
            targetMachine.setMachineName(sourceMachine.getMachineName());
            targetMachine.setMachineTag(sourceMachine.getMachineTag());
            targetMachine.setMachineHost(sourceMachine.getMachineHost());
            targetMachine.setRunStatus(ActionStatus.WAIT_RUNNABLE.getStatus());
            targetMachine.setLogPath(PathBuilders.getReleaseTargetMachineLogPath(targetReleaseId, sourceMachine.getMachineId()));
            targetMachine.setDistPath(sourceMachine.getDistPath());
            // 插入
            releaseMachineDAO.insert(targetMachine);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyReleaseAction(Long sourceReleaseId, Long targetReleaseId) {
        // 查询原始数据
        List<ReleaseActionDO> sourceActions = this.getReleaseAction(sourceReleaseId);
        for (ReleaseActionDO sourceAction : sourceActions) {
            // 构建数据
            ReleaseActionDO releaseAction = new ReleaseActionDO();
            releaseAction.setMachineId(sourceAction.getMachineId());
            releaseAction.setReleaseId(targetReleaseId);
            releaseAction.setActionId(sourceAction.getActionId());
            releaseAction.setActionType(sourceAction.getActionType());
            releaseAction.setActionName(sourceAction.getActionName());
            releaseAction.setActionCommand(sourceAction.getActionCommand());
            releaseAction.setRunStatus(ActionStatus.WAIT_RUNNABLE.getStatus());
            // 插入
            releaseActionDAO.insert(releaseAction);
            ReleaseActionDO updateAction = new ReleaseActionDO();
            updateAction.setId(releaseAction.getId());
            updateAction.setLogPath(PathBuilders.getReleaseActionLogPath(targetReleaseId, releaseAction.getId()));
            // 更新
            releaseActionDAO.updateById(updateAction);
        }
    }

    @Override
    public String getReleaseHostLogPath(Long id) {
        return Optional.ofNullable(releaseBillDAO.selectById(id))
                .map(ReleaseBillDO::getLogPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

    @Override
    public String getReleaseStageLogPath(Long id) {
        return Optional.ofNullable(releaseMachineDAO.selectById(id))
                .map(ReleaseMachineDO::getLogPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

    /**
     * 获取file 尾行数据
     *
     * @param path path
     * @return lines
     */
    private String getFileTailLine(String path) {
        RandomAccessFile reader = null;
        try {
            if (Strings.isBlank(path)) {
                return Strings.EMPTY;
            }
            File file = new File(Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), path));
            // 文件不存在
            if (!Files1.isFile(file)) {
                return Strings.EMPTY;
            }
            // 行数
            String off = MachineEnvAttr.TAIL_OFFSET.getValue();
            int offset;
            if (Strings.isNumber(off)) {
                offset = Integer.valueOf(off);
            } else {
                offset = Const.TAIL_OFFSET_LINE;
            }
            // 获取偏移量
            reader = Files1.openRandomAccess(file, Const.ACCESS_R);
            return FileReaders.readTailLines(reader, offset);
        } catch (IOException e) {
            throw Exceptions.ioRuntime(e);
        } finally {
            Streams.close(reader);
        }
    }

}
