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
package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.constant.Letters;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.collect.Sets;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.CnConst;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.app.PipelineDetailStatus;
import com.orion.ops.constant.app.PipelineStatus;
import com.orion.ops.constant.app.StageType;
import com.orion.ops.constant.app.TimedType;
import com.orion.ops.constant.common.AuditStatus;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.message.MessageType;
import com.orion.ops.constant.user.RoleType;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.app.ApplicationPipelineStageConfigDTO;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.entity.request.app.ApplicationPipelineTaskDetailRequest;
import com.orion.ops.entity.request.app.ApplicationPipelineTaskRequest;
import com.orion.ops.entity.vo.app.*;
import com.orion.ops.handler.app.pipeline.IPipelineProcessor;
import com.orion.ops.handler.app.pipeline.PipelineProcessor;
import com.orion.ops.handler.app.pipeline.PipelineSessionHolder;
import com.orion.ops.service.api.*;
import com.orion.ops.task.TaskRegister;
import com.orion.ops.task.TaskType;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.utils.Valid;
import com.orion.spring.SpringHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用流水线任务服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:57
 */
@Service("applicationPipelineTaskService")
public class ApplicationPipelineTaskServiceImpl implements ApplicationPipelineTaskService {

    @Resource
    private ApplicationPipelineDAO applicationPipelineDAO;

    @Resource
    private ApplicationPipelineDetailService applicationPipelineDetailService;

    @Resource
    private ApplicationPipelineTaskDAO applicationPipelineTaskDAO;

    @Resource
    private ApplicationPipelineTaskDetailDAO applicationPipelineTaskDetailDAO;

    @Resource
    private ApplicationPipelineTaskDetailService applicationPipelineTaskDetailService;

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationBuildService applicationBuildService;

    @Resource
    private ApplicationPipelineTaskLogService applicationPipelineTaskLogService;

    @Resource
    private WebSideMessageService webSideMessageService;

    @Resource
    private PipelineSessionHolder pipelineSessionHolder;

    @Resource
    private TaskRegister taskRegister;

    @Override
    public DataGrid<ApplicationPipelineTaskListVO> getPipelineTaskList(ApplicationPipelineTaskRequest request) {
        LambdaQueryWrapper<ApplicationPipelineTaskDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskDO>()
                .eq(Objects.nonNull(request.getId()), ApplicationPipelineTaskDO::getId, request.getId())
                .eq(Objects.nonNull(request.getProfileId()), ApplicationPipelineTaskDO::getProfileId, request.getProfileId())
                .eq(Objects.nonNull(request.getPipelineId()), ApplicationPipelineTaskDO::getPipelineId, request.getPipelineId())
                .eq(Objects.nonNull(request.getStatus()), ApplicationPipelineTaskDO::getExecStatus, request.getStatus())
                .like(Strings.isNotBlank(request.getPipelineName()), ApplicationPipelineTaskDO::getPipelineName, request.getPipelineName())
                .like(Strings.isNotBlank(request.getTitle()), ApplicationPipelineTaskDO::getExecTitle, request.getTitle())
                .like(Strings.isNotBlank(request.getDescription()), ApplicationPipelineTaskDO::getExecDescription, request.getDescription())
                .orderByDesc(ApplicationPipelineTaskDO::getId);
        // 查询列表
        return DataQuery.of(applicationPipelineTaskDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationPipelineTaskListVO.class);
    }

    @Override
    public ApplicationPipelineTaskVO getPipelineTaskDetail(Long id) {
        // 查询明细
        ApplicationPipelineTaskDO task = applicationPipelineTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.PIPELINE_TASK_ABSENT);
        ApplicationPipelineTaskVO res = Converts.to(task, ApplicationPipelineTaskVO.class);
        // 查询详情
        List<ApplicationPipelineTaskDetailVO> details = applicationPipelineTaskDetailService.getTaskDetails(id);
        res.setDetails(details);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitPipelineTask(ApplicationPipelineTaskRequest request) {
        // 查询流水线
        Long pipelineId = request.getPipelineId();
        ApplicationPipelineDO pipeline = applicationPipelineDAO.selectById(pipelineId);
        Valid.notNull(pipeline, MessageConst.PIPELINE_ABSENT);
        // 查询流水线详情
        List<ApplicationPipelineDetailDO> pipelineDetails = applicationPipelineDetailService.selectByPipelineId(pipelineId);
        Valid.notEmpty(pipelineDetails, MessageConst.PIPELINE_DETAIL_EMPTY);
        // 检查是否存在详情
        Map<Long, ApplicationPipelineTaskDetailRequest> requestDetailsMap = request.getDetails().stream()
                .collect(Collectors.toMap(ApplicationPipelineTaskDetailRequest::getId, Function.identity(), (e1, e2) -> e2));
        boolean detailAllPresent = pipelineDetails.stream()
                .map(ApplicationPipelineDetailDO::getId)
                .map(requestDetailsMap::get)
                .allMatch(Objects::nonNull);
        Valid.isTrue(detailAllPresent, MessageConst.PIPELINE_DETAIL_ABSENT);
        // 设置详情信息
        for (ApplicationPipelineDetailDO pipelineDetail : pipelineDetails) {
            ApplicationPipelineTaskDetailRequest requestDetail = requestDetailsMap.get(pipelineDetail.getId());
            requestDetail.setAppId(pipelineDetail.getAppId());
            requestDetail.setProfileId(pipelineDetail.getProfileId());
            requestDetail.setStageType(pipelineDetail.getStageType());
        }
        // 查询环境信息
        Long profileId = pipeline.getProfileId();
        ApplicationProfileDO profile = applicationProfileDAO.selectById(profileId);
        Valid.notNull(profile, MessageConst.PROFILE_ABSENT);
        // 查询应用信息
        List<Long> appIdList = pipelineDetails.stream()
                .map(ApplicationPipelineDetailDO::getAppId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, ApplicationInfoDO> appInfoMap = applicationInfoDAO.selectBatchIds(appIdList).stream()
                .collect(Collectors.toMap(ApplicationInfoDO::getId, Function.identity(), (e1, e2) -> e2));
        Valid.eq(appIdList.size(), appInfoMap.size(), MessageConst.APP_ABSENT);
        ApplicationPipelineTaskDO pipelineTask = this.setPipelineTask(request, pipeline, profile);
        request.setStatus(pipelineTask.getExecStatus());
        // 插入明细
        applicationPipelineTaskDAO.insert(pipelineTask);
        Long taskId = pipelineTask.getId();
        // 设置详情
        List<ApplicationPipelineTaskDetailDO> taskDetailList = this.setPipelineTaskDetails(taskId, appInfoMap, pipelineDetails, requestDetailsMap);
        taskDetailList.forEach(applicationPipelineTaskDetailDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParams(pipelineTask);
        EventParamsHolder.addParam(EventKeys.DETAILS, taskDetailList);
        return taskId;
    }

    @Override
    public Integer auditPipelineTask(ApplicationPipelineTaskRequest request) {
        // 查询明细
        Long id = request.getId();
        ApplicationPipelineTaskDO task = applicationPipelineTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.PIPELINE_TASK_ABSENT);
        PipelineStatus status = PipelineStatus.of(task.getExecStatus());
        if (!PipelineStatus.WAIT_AUDIT.equals(status) && !PipelineStatus.AUDIT_REJECT.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        AuditStatus auditStatus = AuditStatus.of(request.getAuditStatus());
        UserDTO user = Currents.getUser();
        // 更新
        ApplicationPipelineTaskDO update = new ApplicationPipelineTaskDO();
        update.setId(id);
        update.setAuditUserId(user.getId());
        update.setAuditUserName(user.getUsername());
        update.setAuditTime(new Date());
        update.setAuditReason(request.getAuditReason());
        final boolean resolve = AuditStatus.RESOLVE.equals(auditStatus);
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, task.getId());
        params.put(EventKeys.NAME, task.getPipelineName());
        params.put(EventKeys.TITLE, task.getExecTitle());
        if (resolve) {
            // 通过
            final boolean timedExec = TimedType.TIMED.getType().equals(task.getTimedExec());
            if (!timedExec) {
                update.setExecStatus(PipelineStatus.WAIT_RUNNABLE.getStatus());
            } else {
                update.setExecStatus(PipelineStatus.WAIT_SCHEDULE.getStatus());
                // 提交任务
                taskRegister.submit(TaskType.PIPELINE, task.getTimedExecTime(), id);
            }
            webSideMessageService.addMessage(MessageType.PIPELINE_AUDIT_RESOLVE, id, task.getCreateUserId(), task.getCreateUserName(), params);
        } else {
            // 驳回
            update.setExecStatus(PipelineStatus.AUDIT_REJECT.getStatus());
            webSideMessageService.addMessage(MessageType.PIPELINE_AUDIT_REJECT, id, task.getCreateUserId(), task.getCreateUserName(), params);
        }
        int effect = applicationPipelineTaskDAO.updateById(update);
        this.setEventLogParams(task);
        EventParamsHolder.addParam(EventKeys.OPERATOR, resolve ? CnConst.RESOLVE : CnConst.REJECT);
        return effect;
    }

    @Override
    public Long copyPipeline(Long id) {
        // 查询明细
        ApplicationPipelineTaskDO task = applicationPipelineTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.PIPELINE_TASK_ABSENT);
        // 查询详情
        List<ApplicationPipelineTaskDetailDO> taskDetails = applicationPipelineTaskDetailService.selectTaskDetails(id);
        Valid.notEmpty(taskDetails, MessageConst.PIPELINE_DETAIL_ABSENT);
        // 提交
        ApplicationPipelineTaskRequest request = new ApplicationPipelineTaskRequest();
        request.setPipelineId(task.getPipelineId());
        request.setTitle(task.getExecTitle());
        request.setDescription(task.getExecDescription());
        request.setTimedExec(TimedType.NORMAL.getType());
        // 设置详情
        List<ApplicationPipelineTaskDetailRequest> details = taskDetails.stream().map(s -> {
            ApplicationPipelineStageConfigDTO config = JSON.parseObject(s.getStageConfig(), ApplicationPipelineStageConfigDTO.class);
            ApplicationPipelineTaskDetailRequest detail = Converts.to(config, ApplicationPipelineTaskDetailRequest.class);
            detail.setId(s.getPipelineDetailId());
            return detail;
        }).collect(Collectors.toList());
        request.setDetails(details);
        return SpringHolder.getBean(ApplicationPipelineTaskService.class).submitPipelineTask(request);
    }

    @Override
    public void execPipeline(Long id, boolean isSystem) {
        // 查询明细
        ApplicationPipelineTaskDO task = applicationPipelineTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.PIPELINE_TASK_ABSENT);
        PipelineStatus status = PipelineStatus.of(task.getExecStatus());
        if (!PipelineStatus.WAIT_RUNNABLE.equals(status)
                && !PipelineStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 检查构建版本
        this.checkPipelineExecutable(task);
        // 更新执行人
        ApplicationPipelineTaskDO update = new ApplicationPipelineTaskDO();
        update.setId(id);
        update.setUpdateTime(new Date());
        if (isSystem) {
            update.setExecUserId(task.getCreateUserId());
            update.setExecUserName(task.getCreateUserName());
        } else {
            UserDTO user = Currents.getUser();
            update.setExecUserId(user.getId());
            update.setExecUserName(user.getUsername());
            // 移除
            taskRegister.cancel(TaskType.PIPELINE, id);
        }
        applicationPipelineTaskDAO.updateById(update);
        // 执行
        new PipelineProcessor(id).exec();
        // 设置日志参数
        this.setEventLogParams(task);
        EventParamsHolder.addParam(EventKeys.SYSTEM, isSystem);
    }

    /**
     * 检查流水线是否可执行
     *
     * @param task task
     */
    private void checkPipelineExecutable(ApplicationPipelineTaskDO task) {
        // 检查环境是否存在
        Long profileId = task.getProfileId();
        Valid.notNull(applicationProfileDAO.selectById(profileId), MessageConst.PROFILE_ABSENT);
        // 查询详情
        List<ApplicationPipelineTaskDetailDO> details = applicationPipelineTaskDetailService.selectTaskDetails(task.getId());
        Valid.notEmpty(details, MessageConst.PIPELINE_DETAIL_ABSENT);
        // 检查应用是否存在
        Set<Long> appIdList = details.stream()
                .map(ApplicationPipelineTaskDetailDO::getAppId)
                .collect(Collectors.toSet());
        Map<Long, ApplicationInfoDO> appMap = applicationInfoDAO.selectBatchIds(appIdList).stream()
                .collect(Collectors.toMap(ApplicationInfoDO::getId, Function.identity()));
        // 检查操作详情
        Set<Long> appBuildStage = Sets.newSet();
        for (ApplicationPipelineTaskDetailDO detail : details) {
            // 检查应用是否存在
            Long appId = detail.getAppId();
            ApplicationInfoDO appInfo = appMap.get(appId);
            Valid.notNull(appInfo, detail.getAppName() + MessageConst.ABSENT);
            String appSymbol = CnConst.APP + Strings.SPACE + appInfo.getAppName() + Strings.SPACE;
            // 检查构建版本
            if (StageType.BUILD.getType().equals(detail.getStageType())) {
                appBuildStage.add(appId);
            } else if (StageType.RELEASE.getType().equals(detail.getStageType())) {
                ApplicationPipelineStageConfigDTO stageConfig = JSON.parseObject(detail.getStageConfig(), ApplicationPipelineStageConfigDTO.class);
                Long checkBuildId = stageConfig.getBuildId();
                ApplicationBuildDO checkBuild;
                if (checkBuildId == null) {
                    // 查询最新版本 如果上面有构建操作则跳过
                    boolean hasBuildDetail = appBuildStage.stream().anyMatch(appId::equals);
                    if (hasBuildDetail) {
                        continue;
                    }
                    // 如果没有构建流水线操作则查询最后的构建版本
                    List<ApplicationBuildDO> lastBuild = applicationBuildDAO.selectBuildReleaseList(appId, profileId, 1);
                    Valid.notEmpty(lastBuild, Strings.format(MessageConst.APP_LAST_BUILD_VERSION_ABSENT, appSymbol));
                    checkBuildId = lastBuild.get(0).getId();
                }
                // 查询构建版本
                checkBuild = applicationBuildDAO.selectById(checkBuildId);
                Valid.notNull(checkBuild, appSymbol + MessageConst.BUILD_ABSENT);
                try {
                    // 检查产物是否存在
                    applicationBuildService.checkBuildBundlePath(checkBuild);
                } catch (Exception e) {
                    throw Exceptions.argument(appSymbol + Letters.POUND + checkBuild.getBuildSeq() + Strings.SPACE + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public Integer deletePipeline(List<Long> idList) {
        // 查询明细
        List<ApplicationPipelineTaskDO> pipelineStatus = applicationPipelineTaskDAO.selectBatchIds(idList);
        Valid.notEmpty(pipelineStatus, MessageConst.PIPELINE_TASK_ABSENT);
        boolean canDelete = pipelineStatus.stream()
                .map(ApplicationPipelineTaskDO::getExecStatus)
                .noneMatch(s -> PipelineStatus.WAIT_SCHEDULE.getStatus().equals(s)
                        || PipelineStatus.RUNNABLE.getStatus().equals(s));
        Valid.isTrue(canDelete, MessageConst.ILLEGAL_STATUS);
        // 删除主表
        int effect = applicationPipelineTaskDAO.deleteBatchIds(idList);
        // 删除详情
        effect += applicationPipelineTaskDetailService.deleteByTaskIdList(idList);
        // 删除日志
        effect += applicationPipelineTaskLogService.deleteByTaskIdList(idList);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, idList.size());
        return effect;
    }

    @Override
    public void setPipelineTimedExec(Long id, Date timedExecDate) {
        // 查询明细
        ApplicationPipelineTaskDO task = applicationPipelineTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.PIPELINE_TASK_ABSENT);
        PipelineStatus status = PipelineStatus.of(task.getExecStatus());
        if (!PipelineStatus.WAIT_RUNNABLE.equals(status) && !PipelineStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 取消调度任务
        taskRegister.cancel(TaskType.PIPELINE, id);
        // 更新状态
        ApplicationPipelineTaskDO update = new ApplicationPipelineTaskDO();
        update.setId(id);
        update.setUpdateTime(new Date());
        update.setExecStatus(PipelineStatus.WAIT_SCHEDULE.getStatus());
        update.setTimedExec(TimedType.TIMED.getType());
        update.setTimedExecTime(timedExecDate);
        applicationPipelineTaskDAO.updateById(update);
        // 提交任务
        taskRegister.submit(TaskType.PIPELINE, timedExecDate, id);
        // 设置日志参数
        this.setEventLogParams(task);
        EventParamsHolder.addParam(EventKeys.TIME, Dates.format(timedExecDate));
    }

    @Override
    public void cancelPipelineTimedExec(Long id) {
        // 查询明细
        ApplicationPipelineTaskDO task = applicationPipelineTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.PIPELINE_TASK_ABSENT);
        PipelineStatus status = PipelineStatus.of(task.getExecStatus());
        if (!PipelineStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 更新状态
        ApplicationPipelineTaskDO update = new ApplicationPipelineTaskDO();
        update.setId(id);
        update.setUpdateTime(new Date());
        update.setTimedExec(TimedType.NORMAL.getType());
        update.setExecStatus(PipelineStatus.WAIT_RUNNABLE.getStatus());
        applicationPipelineTaskDAO.updateById(update);
        applicationPipelineTaskDAO.setTimedExecTimeNull(id);
        // 取消调度任务
        taskRegister.cancel(TaskType.PIPELINE, id);
        // 设置日志参数
        this.setEventLogParams(task);
    }

    @Override
    public void terminateExec(Long id) {
        // 查询明细
        ApplicationPipelineTaskDO task = applicationPipelineTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.PIPELINE_TASK_ABSENT);
        PipelineStatus status = PipelineStatus.of(task.getExecStatus());
        if (!PipelineStatus.RUNNABLE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 获取实例
        IPipelineProcessor session = pipelineSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        // 调用终止
        session.terminate();
        // 设置日志参数
        this.setEventLogParams(task);
    }

    @Override
    public void terminateExecDetail(Long id, Long detailId) {
        this.skipOrTerminateTaskDetail(id, detailId, true);
    }

    @Override
    public void skipExecDetail(Long id, Long detailId) {
        this.skipOrTerminateTaskDetail(id, detailId, false);
    }

    /**
     * 跳过或终止任务操作
     *
     * @param id        id
     * @param detailId  detailId
     * @param terminate 终止/跳过
     */
    private void skipOrTerminateTaskDetail(Long id, Long detailId, boolean terminate) {
        // 查询数据
        ApplicationPipelineTaskDO task = applicationPipelineTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.PIPELINE_TASK_ABSENT);
        // 检查状态
        Valid.isTrue(PipelineStatus.RUNNABLE.getStatus().equals(task.getExecStatus()), MessageConst.ILLEGAL_STATUS);
        ApplicationPipelineTaskDetailDO detail = applicationPipelineTaskDetailDAO.selectById(detailId);
        Valid.notNull(detail, MessageConst.UNKNOWN_DATA);
        // 检查状态
        if (terminate) {
            Valid.isTrue(PipelineDetailStatus.RUNNABLE.getStatus().equals(detail.getExecStatus()), MessageConst.ILLEGAL_STATUS);
        } else {
            Valid.isTrue(PipelineDetailStatus.WAIT.getStatus().equals(detail.getExecStatus()), MessageConst.ILLEGAL_STATUS);
        }
        // 停止
        IPipelineProcessor session = pipelineSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        if (terminate) {
            session.terminateDetail(detailId);
        } else {
            session.skipDetail(detailId);
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.DETAIL_ID, detailId);
        EventParamsHolder.addParam(EventKeys.NAME, task.getPipelineName());
        EventParamsHolder.addParam(EventKeys.TITLE, task.getExecTitle());
        EventParamsHolder.addParam(EventKeys.STAGE, StageType.of(detail.getStageType()).getLabel());
        EventParamsHolder.addParam(EventKeys.APP_NAME, detail.getAppName());
    }

    @Override
    public ApplicationPipelineTaskStatusVO getTaskStatus(Long id) {
        // 查询任务状态
        ApplicationPipelineTaskDO task = applicationPipelineTaskDAO.selectStatusById(id);
        Valid.notNull(task, MessageConst.PIPELINE_TASK_ABSENT);
        ApplicationPipelineTaskStatusVO status = Converts.to(task, ApplicationPipelineTaskStatusVO.class);
        // 查询详情状态
        List<ApplicationPipelineTaskDetailDO> details = applicationPipelineTaskDetailDAO.selectStatusByTaskId(id);
        List<ApplicationPipelineTaskDetailStatusVO> detailStatus = Converts.toList(details, ApplicationPipelineTaskDetailStatusVO.class);
        status.setDetails(detailStatus);
        return status;
    }

    @Override
    public List<ApplicationPipelineTaskStatusVO> getTaskStatusList(List<Long> idList, List<Long> detailIdList) {
        // 查询任务状态
        List<ApplicationPipelineTaskDO> taskList = applicationPipelineTaskDAO.selectStatusByIdList(idList);
        List<ApplicationPipelineTaskStatusVO> statusList = Converts.toList(taskList, ApplicationPipelineTaskStatusVO.class);
        // 查询详情状态
        if (Lists.isNotEmpty(detailIdList)) {
            List<ApplicationPipelineTaskDetailDO> detailList = applicationPipelineTaskDetailDAO.selectStatusByIdList(detailIdList);
            for (ApplicationPipelineTaskStatusVO status : statusList) {
                List<ApplicationPipelineTaskDetailDO> detailStatus = detailList.stream()
                        .filter(s -> s.getTaskId().equals(status.getId()))
                        .collect(Collectors.toList());
                status.setDetails(Converts.toList(detailStatus, ApplicationPipelineTaskDetailStatusVO.class));
            }
        }
        return statusList;
    }

    /**
     * 设置流水线明细
     *
     * @param request  request
     * @param pipeline pipeline
     * @param profile  profile
     * @return task
     */
    private ApplicationPipelineTaskDO setPipelineTask(ApplicationPipelineTaskRequest request, ApplicationPipelineDO pipeline, ApplicationProfileDO profile) {
        // 获取指定构建版本的发布操作
        List<Long> specifiedBuildList = request.getDetails().stream()
                .filter(s -> StageType.RELEASE.getType().equals(s.getStageType()))
                .map(ApplicationPipelineTaskDetailRequest::getBuildId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!specifiedBuildList.isEmpty()) {
            List<ApplicationBuildDO> buildList = applicationBuildDAO.selectBatchIds(specifiedBuildList);
            Valid.isTrue(buildList.size() == specifiedBuildList.size(), MessageConst.BUILD_ABSENT);
            // 检查构建产物是否存在
            for (ApplicationBuildDO build : buildList) {
                try {
                    applicationBuildService.checkBuildBundlePath(build);
                } catch (Exception e) {
                    String message = Strings.format("{} #{} {}", build.getAppName(), build.getBuildSeq(), e.getMessage());
                    throw Exceptions.argument(message, e);
                }
            }
        }
        // 设置流水线
        UserDTO user = Currents.getUser();
        ApplicationPipelineTaskDO pipelineTask = new ApplicationPipelineTaskDO();
        pipelineTask.setPipelineId(pipeline.getId());
        pipelineTask.setPipelineName(pipeline.getPipelineName());
        pipelineTask.setProfileId(profile.getId());
        pipelineTask.setProfileName(profile.getProfileName());
        pipelineTask.setProfileTag(profile.getProfileTag());
        pipelineTask.setExecTitle(request.getTitle());
        pipelineTask.setExecDescription(request.getDescription());
        pipelineTask.setTimedExec(request.getTimedExec());
        pipelineTask.setTimedExecTime(request.getTimedExecTime());
        pipelineTask.setCreateUserId(user.getId());
        pipelineTask.setCreateUserName(user.getUsername());
        // 设置审核信息
        this.setCreateAuditInfo(pipelineTask, user, Const.ENABLE.equals(profile.getReleaseAudit()));
        return pipelineTask;
    }

    /**
     * 设置流水线详情明细
     *
     * @param taskId            taskId
     * @param appInfoMap        appInfoMap
     * @param pipelineDetails   pipelineDetails
     * @param requestDetailsMap requestDetailsMap
     * @return details
     */
    private List<ApplicationPipelineTaskDetailDO> setPipelineTaskDetails(Long taskId, Map<Long, ApplicationInfoDO> appInfoMap,
                                                                         List<ApplicationPipelineDetailDO> pipelineDetails,
                                                                         Map<Long, ApplicationPipelineTaskDetailRequest> requestDetailsMap) {
        List<ApplicationPipelineTaskDetailDO> taskDetailList = Lists.newList();
        for (ApplicationPipelineDetailDO pipelineDetail : pipelineDetails) {
            // 设置流水操作
            ApplicationPipelineTaskDetailDO taskDetail = new ApplicationPipelineTaskDetailDO();
            taskDetail.setPipelineId(pipelineDetail.getPipelineId());
            taskDetail.setPipelineDetailId(pipelineDetail.getId());
            taskDetail.setTaskId(taskId);
            // 应用
            ApplicationInfoDO app = appInfoMap.get(pipelineDetail.getAppId());
            taskDetail.setAppId(app.getId());
            taskDetail.setAppName(app.getAppName());
            taskDetail.setAppTag(app.getAppTag());
            taskDetail.setStageType(pipelineDetail.getStageType());
            // 阶段配置
            ApplicationPipelineTaskDetailRequest requestDetail = requestDetailsMap.get(pipelineDetail.getId());
            ApplicationPipelineStageConfigDTO stageConfig = Converts.to(requestDetail, ApplicationPipelineStageConfigDTO.class);
            taskDetail.setStageConfig(JSON.toJSONString(stageConfig));
            taskDetail.setExecStatus(PipelineDetailStatus.WAIT.getStatus());
            taskDetailList.add(taskDetail);
        }
        return taskDetailList;
    }

    /**
     * 创建时设置是否需要审核
     *
     * @param task      task
     * @param user      user
     * @param needAudit needAudit
     */
    private void setCreateAuditInfo(ApplicationPipelineTaskDO task, UserDTO user, boolean needAudit) {
        boolean isAdmin = RoleType.isAdministrator(user.getRoleType());
        // 需要审核 & 不是管理员
        if (needAudit && !isAdmin) {
            task.setExecStatus(PipelineStatus.WAIT_AUDIT.getStatus());
        } else {
            if (TimedType.TIMED.getType().equals(task.getTimedExec())) {
                task.setExecStatus(PipelineStatus.WAIT_SCHEDULE.getStatus());
            } else {
                task.setExecStatus(PipelineStatus.WAIT_RUNNABLE.getStatus());
            }
            task.setAuditUserId(user.getId());
            task.setAuditUserName(user.getUsername());
            task.setAuditTime(new Date());
            if (isAdmin) {
                task.setAuditReason(MessageConst.AUTO_AUDIT_RESOLVE);
            } else {
                task.setAuditReason(MessageConst.AUDIT_NOT_REQUIRED);
            }
        }
    }

    /**
     * 设置操作日志参数
     *
     * @param task task
     */
    private void setEventLogParams(ApplicationPipelineTaskDO task) {
        EventParamsHolder.addParam(EventKeys.ID, task.getId());
        EventParamsHolder.addParam(EventKeys.PIPELINE_ID, task.getPipelineId());
        EventParamsHolder.addParam(EventKeys.NAME, task.getPipelineName());
        EventParamsHolder.addParam(EventKeys.TITLE, task.getExecTitle());
    }

}
