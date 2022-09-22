package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.DataClearRange;
import com.orion.ops.constant.app.BuildStatus;
import com.orion.ops.constant.app.PipelineStatus;
import com.orion.ops.constant.app.ReleaseStatus;
import com.orion.ops.constant.command.ExecStatus;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.scheduler.SchedulerTaskStatus;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.request.data.DataClearRequest;
import com.orion.ops.service.api.DataClearService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.EventParamsHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 数据清理服务实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/24 17:04
 */
@Service("dataClearService")
public class DataClearServiceImpl implements DataClearService {

    @Resource
    private CommandExecDAO commandExecDAO;

    @Resource
    private MachineTerminalLogDAO machineTerminalLogDAO;

    @Resource
    private SchedulerTaskRecordDAO schedulerTaskRecordDAO;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationReleaseDAO applicationReleaseDAO;

    @Resource
    private ApplicationPipelineTaskDAO applicationPipelineTaskDAO;

    @Resource
    private UserEventLogDAO userEventLogDAO;

    @Resource
    private MachineAlarmHistoryDAO machineAlarmHistoryDAO;

    @Override
    public Integer clearBatchExec(DataClearRequest request) {
        if (!Currents.isAdministrator()) {
            request.setICreated(Const.ENABLE);
        }
        // 基础删除条件
        LambdaQueryWrapper<CommandExecDO> wrapper = new LambdaQueryWrapper<CommandExecDO>()
                .ne(CommandExecDO::getExecStatus, ExecStatus.RUNNABLE.getStatus())
                .eq(Const.ENABLE.equals(request.getICreated()), CommandExecDO::getUserId, Currents.getUserId());
        // 设置删除筛选条件
        this.setDeleteWrapper(commandExecDAO, wrapper,
                CommandExecDO::getId,
                CommandExecDO::getCreateTime,
                CommandExecDO::getMachineId,
                request);
        int count = commandExecDAO.delete(wrapper);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.COUNT, count);
        return count;
    }

    @Override
    public Integer clearTerminalLog(DataClearRequest request) {
        LambdaQueryWrapper<MachineTerminalLogDO> wrapper = new LambdaQueryWrapper<>();
        // 设置删除筛选条件
        this.setDeleteWrapper(machineTerminalLogDAO, wrapper,
                MachineTerminalLogDO::getId,
                MachineTerminalLogDO::getCreateTime,
                MachineTerminalLogDO::getMachineId,
                request);
        int count = machineTerminalLogDAO.delete(wrapper);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.COUNT, count);
        return count;
    }

    @Override
    public Integer clearSchedulerRecord(DataClearRequest request) {
        LambdaQueryWrapper<SchedulerTaskRecordDO> wrapper = new LambdaQueryWrapper<SchedulerTaskRecordDO>()
                .ne(SchedulerTaskRecordDO::getTaskStatus, SchedulerTaskStatus.RUNNABLE.getStatus())
                .eq(SchedulerTaskRecordDO::getTaskId, request.getRelId());
        // 设置删除筛选条件
        this.setDeleteWrapper(schedulerTaskRecordDAO, wrapper,
                SchedulerTaskRecordDO::getId,
                SchedulerTaskRecordDO::getCreateTime,
                SchedulerTaskRecordDO::getTaskId,
                request);
        int count = schedulerTaskRecordDAO.delete(wrapper);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.COUNT, count);
        return count;
    }

    @Override
    public Integer clearAppBuild(DataClearRequest request) {
        Long userId = Currents.getUserId();
        LambdaQueryWrapper<ApplicationBuildDO> wrapper = new LambdaQueryWrapper<ApplicationBuildDO>()
                .ne(ApplicationBuildDO::getBuildStatus, BuildStatus.RUNNABLE.getStatus())
                .eq(ApplicationBuildDO::getProfileId, request.getProfileId())
                .eq(Const.ENABLE.equals(request.getICreated()), ApplicationBuildDO::getCreateUserId, userId);
        // 设置筛选条件
        this.setDeleteWrapper(applicationBuildDAO, wrapper,
                ApplicationBuildDO::getId,
                ApplicationBuildDO::getCreateTime,
                ApplicationBuildDO::getAppId,
                request);
        int count = applicationBuildDAO.delete(wrapper);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.COUNT, count);
        return count;
    }

    @Override
    public Integer clearAppRelease(DataClearRequest request) {
        Long userId = Currents.getUserId();
        LambdaQueryWrapper<ApplicationReleaseDO> wrapper = new LambdaQueryWrapper<ApplicationReleaseDO>()
                .ne(ApplicationReleaseDO::getReleaseStatus, ReleaseStatus.RUNNABLE.getStatus())
                .eq(ApplicationReleaseDO::getProfileId, request.getProfileId())
                .eq(Const.ENABLE.equals(request.getICreated()), ApplicationReleaseDO::getCreateUserId, userId)
                .eq(Const.ENABLE.equals(request.getIAudited()), ApplicationReleaseDO::getAuditUserId, userId)
                .eq(Const.ENABLE.equals(request.getIExecute()), ApplicationReleaseDO::getReleaseUserId, userId);
        // 设置筛选条件
        this.setDeleteWrapper(applicationReleaseDAO, wrapper,
                ApplicationReleaseDO::getId,
                ApplicationReleaseDO::getCreateTime,
                ApplicationReleaseDO::getAppId,
                request);
        int count = applicationReleaseDAO.delete(wrapper);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.COUNT, count);
        return count;
    }

    @Override
    public Integer clearAppPipeline(DataClearRequest request) {
        Long userId = Currents.getUserId();
        LambdaQueryWrapper<ApplicationPipelineTaskDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskDO>()
                .ne(ApplicationPipelineTaskDO::getExecStatus, PipelineStatus.RUNNABLE.getStatus())
                .eq(ApplicationPipelineTaskDO::getProfileId, request.getProfileId())
                .eq(Const.ENABLE.equals(request.getICreated()), ApplicationPipelineTaskDO::getCreateUserId, userId)
                .eq(Const.ENABLE.equals(request.getIAudited()), ApplicationPipelineTaskDO::getAuditUserId, userId)
                .eq(Const.ENABLE.equals(request.getIExecute()), ApplicationPipelineTaskDO::getExecUserId, userId);
        // 设置筛选条件
        this.setDeleteWrapper(applicationPipelineTaskDAO, wrapper,
                ApplicationPipelineTaskDO::getId,
                ApplicationPipelineTaskDO::getCreateTime,
                ApplicationPipelineTaskDO::getPipelineId,
                request);
        int count = applicationPipelineTaskDAO.delete(wrapper);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.COUNT, count);
        return count;
    }

    @Override
    public Integer clearEventLog(DataClearRequest request) {
        LambdaQueryWrapper<UserEventLogDO> wrapper = new LambdaQueryWrapper<UserEventLogDO>()
                .eq(UserEventLogDO::getExecResult, Const.ENABLE);
        // 设置删除筛选条件
        this.setDeleteWrapper(userEventLogDAO, wrapper,
                UserEventLogDO::getId,
                UserEventLogDO::getCreateTime,
                UserEventLogDO::getEventClassify,
                request);
        int count = userEventLogDAO.delete(wrapper);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.COUNT, count);
        return count;
    }

    @Override
    public Integer clearMachineAlarmHistory(DataClearRequest request) {
        LambdaQueryWrapper<MachineAlarmHistoryDO> wrapper = new LambdaQueryWrapper<MachineAlarmHistoryDO>()
                .eq(MachineAlarmHistoryDO::getMachineId, request.getMachineId());
        // 设置删除筛选条件
        this.setDeleteWrapper(machineAlarmHistoryDAO, wrapper,
                MachineAlarmHistoryDO::getId,
                MachineAlarmHistoryDO::getCreateTime,
                MachineAlarmHistoryDO::getMachineId,
                request);
        int count = machineAlarmHistoryDAO.delete(wrapper);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.COUNT, count);
        return count;
    }

    /**
     * 设置删除的筛选条件
     *
     * @param mapper        mapper
     * @param wrapper       wrapper
     * @param idGetterFun   idGetterFun
     * @param dateGetterFun dateGetterFun
     * @param relGetterFun  relGetterFun
     * @param request       request
     * @param <T>           <T>
     */
    private <T> void setDeleteWrapper(BaseMapper<T> mapper,
                                      LambdaQueryWrapper<T> wrapper,
                                      SFunction<T, Long> idGetterFun,
                                      SFunction<T, Date> dateGetterFun,
                                      SFunction<T, ?> relGetterFun,
                                      DataClearRequest request) {
        if (DataClearRange.DAY.getRange().equals(request.getRange())) {
            // 仅保留几天
            Integer day = request.getReserveDay();
            if (!day.equals(0)) {
                this.setReverseDateWhere(wrapper, dateGetterFun, day);
            }
        } else if (DataClearRange.TOTAL.getRange().equals(request.getRange())) {
            // 保留几条
            Integer total = request.getReserveTotal();
            if (!total.equals(0)) {
                this.setReverseTotalWhere(mapper, wrapper, idGetterFun, total);
            }
        } else if (DataClearRange.REL_ID.getRange().equals(request.getRange())) {
            // 删除机器id
            wrapper.in(relGetterFun, request.getRelIdList());
        }
    }

    /**
     * 设置保留多少天的筛选条件
     *
     * @param wrapper       wrapper
     * @param dateGetterFun dateGetterFun
     * @param day           day
     * @param <T>           T
     */
    private <T> void setReverseDateWhere(LambdaQueryWrapper<T> wrapper,
                                         SFunction<T, Date> dateGetterFun,
                                         Integer day) {
        Date lessDayDate = Dates.stream().subDay(day).get();
        wrapper.lt(dateGetterFun, lessDayDate);
    }

    /**
     * 设置保留条数的筛选条件
     *
     * @param mapper      mapper
     * @param wrapper     wrapper
     * @param idGetterFun idGetterFun
     * @param total       total
     * @param <T>         T
     */
    private <T> void setReverseTotalWhere(BaseMapper<T> mapper,
                                          LambdaQueryWrapper<T> wrapper,
                                          SFunction<T, Long> idGetterFun,
                                          Integer total) {
        // 查询删除的最大id
        LambdaQueryWrapper<T> maxIdWrapper = wrapper.clone()
                .orderByDesc(idGetterFun)
                .last(Const.LIMIT + Const.SPACE + total + ", 1");
        T maxEntity = mapper.selectOne(maxIdWrapper);
        // 未查询到则代表条数不满足, 设置一个 false 条件 从而达到不执行的目的
        if (maxEntity == null) {
            wrapper.eq(idGetterFun, Const.L_N_1);
            return;
        }
        Long maxId = idGetterFun.apply(maxEntity);
        // 设置最大id阈值
        wrapper.le(idGetterFun, maxId);
    }

}
