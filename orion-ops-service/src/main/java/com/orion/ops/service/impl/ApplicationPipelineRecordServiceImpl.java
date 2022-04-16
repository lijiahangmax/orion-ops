package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.constant.Letters;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.AuditStatus;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.PipelineDetailStatus;
import com.orion.ops.consts.app.PipelineStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.consts.app.TimedType;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.event.EventParamsHolder;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.ApplicationPipelineStageConfigDTO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.ApplicationPipelineDetailRecordRequest;
import com.orion.ops.entity.request.ApplicationPipelineRecordRequest;
import com.orion.ops.entity.vo.ApplicationPipelineDetailRecordVO;
import com.orion.ops.entity.vo.ApplicationPipelineRecordListVO;
import com.orion.ops.entity.vo.ApplicationPipelineRecordVO;
import com.orion.ops.handler.app.pipeline.IPipelineProcessor;
import com.orion.ops.handler.app.pipeline.PipelineProcessor;
import com.orion.ops.handler.app.pipeline.PipelineSessionHolder;
import com.orion.ops.service.api.ApplicationBuildService;
import com.orion.ops.service.api.ApplicationPipelineDetailRecordService;
import com.orion.ops.service.api.ApplicationPipelineDetailService;
import com.orion.ops.service.api.ApplicationPipelineRecordService;
import com.orion.ops.task.TaskRegister;
import com.orion.ops.task.TaskType;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Sets;
import com.orion.utils.convert.Converts;
import com.orion.utils.time.Dates;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用流水线明细服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:57
 */
@Service("applicationPipelineRecordService")
public class ApplicationPipelineRecordServiceImpl implements ApplicationPipelineRecordService {

    @Resource
    private ApplicationPipelineDAO applicationPipelineDAO;

    @Resource
    private ApplicationPipelineDetailService applicationPipelineDetailService;

    @Resource
    private ApplicationPipelineRecordDAO applicationPipelineRecordDAO;

    @Resource
    private ApplicationPipelineDetailRecordDAO applicationPipelineDetailRecordDAO;

    @Resource
    private ApplicationPipelineDetailRecordService applicationPipelineDetailRecordService;

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationBuildService applicationBuildService;

    @Resource
    private PipelineSessionHolder pipelineSessionHolder;

    @Resource
    private TaskRegister taskRegister;

    @Override
    public DataGrid<ApplicationPipelineRecordListVO> getPipelineRecordList(ApplicationPipelineRecordRequest request) {
        LambdaQueryWrapper<ApplicationPipelineRecordDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineRecordDO>()
                .eq(Objects.nonNull(request.getProfileId()), ApplicationPipelineRecordDO::getProfileId, request.getProfileId())
                .eq(Objects.nonNull(request.getPipelineId()), ApplicationPipelineRecordDO::getPipelineId, request.getPipelineId())
                .eq(Objects.nonNull(request.getStatus()), ApplicationPipelineRecordDO::getExecStatus, request.getStatus())
                .like(Strings.isNotBlank(request.getPipelineName()), ApplicationPipelineRecordDO::getPipelineName, request.getPipelineName())
                .like(Strings.isNotBlank(request.getTitle()), ApplicationPipelineRecordDO::getExecTitle, request.getTitle())
                .like(Strings.isNotBlank(request.getDescription()), ApplicationPipelineRecordDO::getExecDescription, request.getDescription())
                .orderByDesc(ApplicationPipelineRecordDO::getId);
        // 查询列表
        return DataQuery.of(applicationPipelineRecordDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationPipelineRecordListVO.class);
    }

    @Override
    public ApplicationPipelineRecordVO getPipelineRecordDetail(Long id) {
        // 查询明细
        ApplicationPipelineRecordDO record = applicationPipelineRecordDAO.selectById(id);
        Valid.notNull(record, MessageConst.PIPELINE_ABSENT);
        ApplicationPipelineRecordVO res = Converts.to(record, ApplicationPipelineRecordVO.class);
        // 查询详情
        List<ApplicationPipelineDetailRecordVO> details = applicationPipelineDetailRecordService.getRecordDetails(id);
        res.setDetails(details);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitPipelineExec(ApplicationPipelineRecordRequest request) {
        // 查询流水线
        Long pipelineId = request.getPipelineId();
        ApplicationPipelineDO pipeline = applicationPipelineDAO.selectById(pipelineId);
        Valid.notNull(pipeline, MessageConst.PIPELINE_ABSENT);
        // 查询流水线详情
        List<ApplicationPipelineDetailDO> pipelineDetails = applicationPipelineDetailService.selectByPipelineId(pipelineId);
        Valid.notEmpty(pipelineDetails, MessageConst.PIPELINE_DETAIL_EMPTY);
        // 检查是否存在详情
        Map<Long, ApplicationPipelineDetailRecordRequest> requestDetailsMap = request.getDetails().stream()
                .collect(Collectors.toMap(ApplicationPipelineDetailRecordRequest::getId, Function.identity(), (e1, e2) -> e2));
        boolean detailAllPresent = pipelineDetails.stream()
                .map(ApplicationPipelineDetailDO::getId)
                .map(requestDetailsMap::get)
                .allMatch(Objects::nonNull);
        Valid.isTrue(detailAllPresent, MessageConst.PIPELINE_DETAIL_ABSENT);
        // 设置详情信息
        for (ApplicationPipelineDetailDO pipelineDetail : pipelineDetails) {
            ApplicationPipelineDetailRecordRequest requestDetail = requestDetailsMap.get(pipelineDetail.getId());
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
        ApplicationPipelineRecordDO pipelineRecord = this.setPipelineRecord(request, pipeline, profile);
        request.setStatus(pipelineRecord.getExecStatus());
        // 插入明细
        applicationPipelineRecordDAO.insert(pipelineRecord);
        Long recordId = pipelineRecord.getId();
        // 设置详情
        List<ApplicationPipelineDetailRecordDO> recordDetailList = this.setPipelineDetailRecords(recordId, appInfoMap, pipelineDetails, requestDetailsMap);
        recordDetailList.forEach(applicationPipelineDetailRecordDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParams(pipelineRecord);
        EventParamsHolder.addParam(EventKeys.DETAILS, recordDetailList);
        return recordId;
    }

    @Override
    public Integer auditPipeline(ApplicationPipelineRecordRequest request) {
        // 查询明细
        Long id = request.getId();
        ApplicationPipelineRecordDO record = applicationPipelineRecordDAO.selectById(id);
        Valid.notNull(record, MessageConst.PIPELINE_ABSENT);
        PipelineStatus status = PipelineStatus.of(record.getExecStatus());
        if (!PipelineStatus.WAIT_AUDIT.equals(status) && !PipelineStatus.AUDIT_REJECT.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        AuditStatus auditStatus = AuditStatus.of(request.getAuditStatus());
        UserDTO user = Currents.getUser();
        // 更新
        ApplicationPipelineRecordDO update = new ApplicationPipelineRecordDO();
        update.setId(id);
        update.setAuditUserId(user.getId());
        update.setAuditUserName(user.getUsername());
        update.setAuditTime(new Date());
        update.setAuditReason(request.getAuditReason());
        final boolean resolve = AuditStatus.RESOLVE.equals(auditStatus);
        // 发送站内信
        if (resolve) {
            // 通过
            final boolean timedExec = TimedType.TIMED.getType().equals(record.getTimedExec());
            if (!timedExec) {
                update.setExecStatus(PipelineStatus.WAIT_RUNNABLE.getStatus());
            } else {
                update.setExecStatus(PipelineStatus.WAIT_SCHEDULE.getStatus());
                // 提交任务
                taskRegister.submit(TaskType.PIPELINE, record.getTimedExecTime(), id);
            }
        } else {
            // 驳回
            update.setExecStatus(PipelineStatus.AUDIT_REJECT.getStatus());
        }
        int effect = applicationPipelineRecordDAO.updateById(update);
        this.setEventLogParams(record);
        EventParamsHolder.addParam(EventKeys.OPERATOR, resolve ? Const.RESOLVE_LABEL : Const.REJECT_LABEL);
        return effect;
    }

    @Override
    public Long copyPipeline(Long id) {
        // 查询明细
        ApplicationPipelineRecordDO record = applicationPipelineRecordDAO.selectById(id);
        Valid.notNull(record, MessageConst.PIPELINE_ABSENT);
        // 查询详情
        List<ApplicationPipelineDetailRecordDO> recordDetails = applicationPipelineDetailRecordService.selectRecordDetails(id);
        Valid.notEmpty(recordDetails, MessageConst.PIPELINE_DETAIL_ABSENT);
        // 提交
        ApplicationPipelineRecordRequest request = new ApplicationPipelineRecordRequest();
        request.setPipelineId(record.getPipelineId());
        request.setTitle(record.getExecTitle());
        request.setDescription(record.getExecDescription());
        request.setTimedExec(TimedType.NORMAL.getType());
        // 设置详情
        List<ApplicationPipelineDetailRecordRequest> details = recordDetails.stream().map(s -> {
            ApplicationPipelineStageConfigDTO config = JSON.parseObject(s.getStageConfig(), ApplicationPipelineStageConfigDTO.class);
            ApplicationPipelineDetailRecordRequest detail = Converts.to(config, ApplicationPipelineDetailRecordRequest.class);
            detail.setId(s.getPipelineDetailId());
            return detail;
        }).collect(Collectors.toList());
        request.setDetails(details);
        return SpringHolder.getBean(ApplicationPipelineRecordService.class).submitPipelineExec(request);
    }

    @Override
    public void execPipeline(Long id, boolean isSystem) {
        // 查询明细
        ApplicationPipelineRecordDO record = applicationPipelineRecordDAO.selectById(id);
        Valid.notNull(record, MessageConst.PIPELINE_ABSENT);
        PipelineStatus status = PipelineStatus.of(record.getExecStatus());
        if (!PipelineStatus.WAIT_RUNNABLE.equals(status)
                && !PipelineStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 检查构建版本
        this.checkPipelineExecutable(record);
        // 更新执行人
        ApplicationPipelineRecordDO update = new ApplicationPipelineRecordDO();
        update.setId(id);
        update.setUpdateTime(new Date());
        if (isSystem) {
            update.setExecUserId(record.getCreateUserId());
            update.setExecUserName(record.getCreateUserName());
        } else {
            UserDTO user = Currents.getUser();
            update.setExecUserId(user.getId());
            update.setExecUserName(user.getUsername());
            // 移除
            taskRegister.cancel(TaskType.PIPELINE, id);
        }
        applicationPipelineRecordDAO.updateById(update);
        // 执行
        new PipelineProcessor(id).exec();
        // 设置日志参数
        this.setEventLogParams(record);
    }

    /**
     * 检查流水线是否可执行
     *
     * @param record record
     */
    private void checkPipelineExecutable(ApplicationPipelineRecordDO record) {
        // 检查环境是否存在
        Long profileId = record.getProfileId();
        Valid.notNull(applicationProfileDAO.selectById(profileId), MessageConst.PROFILE_ABSENT);
        // 查询详情
        List<ApplicationPipelineDetailRecordDO> details = applicationPipelineDetailRecordService.selectRecordDetails(record.getId());
        Valid.notEmpty(details, MessageConst.PIPELINE_DETAIL_ABSENT);
        // 检查应用是否存在
        Set<Long> appIdList = details.stream()
                .map(ApplicationPipelineDetailRecordDO::getAppId)
                .collect(Collectors.toSet());
        Map<Long, ApplicationInfoDO> appMap = applicationInfoDAO.selectBatchIds(appIdList).stream()
                .collect(Collectors.toMap(ApplicationInfoDO::getId, Function.identity()));
        // 检查操作详情
        Set<Long> appBuildStage = Sets.newSet();
        for (ApplicationPipelineDetailRecordDO detail : details) {
            // 检查应用是否存在
            Long appId = detail.getAppId();
            ApplicationInfoDO appInfo = appMap.get(appId);
            Valid.notNull(appInfo, detail.getAppName() + MessageConst.ABSENT);
            String appSymbol = Const.APP_CN + Strings.SPACE + appInfo.getAppName() + Strings.SPACE;
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
        List<ApplicationPipelineRecordDO> pipelineStatus = applicationPipelineRecordDAO.selectBatchIds(idList);
        Valid.notEmpty(pipelineStatus, MessageConst.PIPELINE_ABSENT);
        boolean canDelete = pipelineStatus.stream()
                .map(ApplicationPipelineRecordDO::getExecStatus)
                .noneMatch(s -> PipelineStatus.WAIT_SCHEDULE.getStatus().equals(s)
                        || PipelineStatus.RUNNABLE.getStatus().equals(s));
        Valid.isTrue(canDelete, MessageConst.ILLEGAL_STATUS);
        // 删除主表
        int effect = applicationPipelineRecordDAO.deleteBatchIds(idList);
        // 删除详情
        effect += applicationPipelineDetailRecordService.deleteByRecordIdList(idList);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, idList.size());
        return effect;
    }

    @Override
    public void setPipelineTimedExec(Long id, Date timedExecDate) {
        // 查询明细
        ApplicationPipelineRecordDO record = applicationPipelineRecordDAO.selectById(id);
        Valid.notNull(record, MessageConst.PIPELINE_ABSENT);
        PipelineStatus status = PipelineStatus.of(record.getExecStatus());
        if (!PipelineStatus.WAIT_RUNNABLE.equals(status) && !PipelineStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 取消调度任务
        taskRegister.cancel(TaskType.PIPELINE, id);
        // 更新状态
        ApplicationPipelineRecordDO update = new ApplicationPipelineRecordDO();
        update.setId(id);
        update.setUpdateTime(new Date());
        update.setExecStatus(PipelineStatus.WAIT_SCHEDULE.getStatus());
        update.setTimedExec(TimedType.TIMED.getType());
        update.setTimedExecTime(timedExecDate);
        applicationPipelineRecordDAO.updateById(update);
        // 提交任务
        taskRegister.submit(TaskType.PIPELINE, timedExecDate, id);
        // 设置日志参数
        this.setEventLogParams(record);
        EventParamsHolder.addParam(EventKeys.TIME, Dates.format(timedExecDate));
    }

    @Override
    public void cancelPipelineTimedExec(Long id) {
        // 查询明细
        ApplicationPipelineRecordDO record = applicationPipelineRecordDAO.selectById(id);
        Valid.notNull(record, MessageConst.PIPELINE_ABSENT);
        PipelineStatus status = PipelineStatus.of(record.getExecStatus());
        if (!PipelineStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 更新状态
        ApplicationPipelineRecordDO update = new ApplicationPipelineRecordDO();
        update.setId(id);
        update.setUpdateTime(new Date());
        update.setTimedExec(TimedType.NORMAL.getType());
        update.setExecStatus(PipelineStatus.WAIT_RUNNABLE.getStatus());
        applicationPipelineRecordDAO.updateById(update);
        applicationPipelineRecordDAO.setTimedExecTimeNull(id);
        // 取消调度任务
        taskRegister.cancel(TaskType.PIPELINE, id);
        // 设置日志参数
        this.setEventLogParams(record);
    }

    @Override
    public void terminatedExec(Long id) {
        // 查询明细
        ApplicationPipelineRecordDO record = applicationPipelineRecordDAO.selectById(id);
        Valid.notNull(record, MessageConst.PIPELINE_ABSENT);
        PipelineStatus status = PipelineStatus.of(record.getExecStatus());
        if (!PipelineStatus.RUNNABLE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 获取实例
        IPipelineProcessor session = pipelineSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        // 调用终止
        session.terminated();
        // 设置日志参数
        this.setEventLogParams(record);
    }

    /**
     * 设置流水线明细
     *
     * @param request  request
     * @param pipeline pipeline
     * @param profile  profile
     * @return record
     */
    private ApplicationPipelineRecordDO setPipelineRecord(ApplicationPipelineRecordRequest request, ApplicationPipelineDO pipeline, ApplicationProfileDO profile) {
        // 获取指定构建版本的发布操作
        List<Long> specifiedBuildList = request.getDetails().stream()
                .filter(s -> StageType.RELEASE.getType().equals(s.getStageType()))
                .map(ApplicationPipelineDetailRecordRequest::getBuildId)
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
        ApplicationPipelineRecordDO pipelineRecord = new ApplicationPipelineRecordDO();
        pipelineRecord.setPipelineId(pipeline.getId());
        pipelineRecord.setPipelineName(pipeline.getPipelineName());
        pipelineRecord.setProfileId(profile.getId());
        pipelineRecord.setProfileName(profile.getProfileName());
        pipelineRecord.setProfileTag(profile.getProfileTag());
        pipelineRecord.setExecTitle(request.getTitle());
        pipelineRecord.setExecDescription(request.getDescription());
        pipelineRecord.setTimedExec(request.getTimedExec());
        pipelineRecord.setTimedExecTime(request.getTimedExecTime());
        pipelineRecord.setCreateUserId(user.getId());
        pipelineRecord.setCreateUserName(user.getUsername());
        // 设置审核信息
        this.setCreateAuditInfo(pipelineRecord, user, Const.ENABLE.equals(profile.getReleaseAudit()));
        return pipelineRecord;
    }

    /**
     * 设置流水线详情明细
     *
     * @param recordId          recordId
     * @param appInfoMap        appInfoMap
     * @param pipelineDetails   pipelineDetails
     * @param requestDetailsMap requestDetailsMap
     * @return details
     */
    private List<ApplicationPipelineDetailRecordDO> setPipelineDetailRecords(Long recordId, Map<Long, ApplicationInfoDO> appInfoMap,
                                                                             List<ApplicationPipelineDetailDO> pipelineDetails,
                                                                             Map<Long, ApplicationPipelineDetailRecordRequest> requestDetailsMap) {
        List<ApplicationPipelineDetailRecordDO> recordDetailList = Lists.newList();
        for (ApplicationPipelineDetailDO pipelineDetail : pipelineDetails) {
            // 设置流水操作
            ApplicationPipelineDetailRecordDO detailRecord = new ApplicationPipelineDetailRecordDO();
            detailRecord.setPipelineId(pipelineDetail.getPipelineId());
            detailRecord.setPipelineDetailId(pipelineDetail.getId());
            detailRecord.setRecordId(recordId);
            // 应用
            ApplicationInfoDO app = appInfoMap.get(pipelineDetail.getAppId());
            detailRecord.setAppId(app.getId());
            detailRecord.setAppName(app.getAppName());
            detailRecord.setAppTag(app.getAppTag());
            detailRecord.setStageType(pipelineDetail.getStageType());
            // 阶段配置
            ApplicationPipelineDetailRecordRequest requestDetail = requestDetailsMap.get(pipelineDetail.getId());
            ApplicationPipelineStageConfigDTO stageConfig = Converts.to(requestDetail, ApplicationPipelineStageConfigDTO.class);
            detailRecord.setStageConfig(JSON.toJSONString(stageConfig));
            detailRecord.setExecStatus(PipelineDetailStatus.WAIT.getStatus());
            recordDetailList.add(detailRecord);
        }
        return recordDetailList;
    }

    /**
     * 创建时设置是否需要审核
     *
     * @param pipelineRecord pipelineRecord
     * @param user           user
     * @param needAudit      needAudit
     */
    private void setCreateAuditInfo(ApplicationPipelineRecordDO pipelineRecord, UserDTO user, boolean needAudit) {
        boolean isAdmin = RoleType.isAdministrator(user.getRoleType());
        // 需要审核 & 不是管理员
        if (needAudit && !isAdmin) {
            pipelineRecord.setExecStatus(PipelineStatus.WAIT_AUDIT.getStatus());
        } else {
            if (TimedType.TIMED.getType().equals(pipelineRecord.getTimedExec())) {
                pipelineRecord.setExecStatus(PipelineStatus.WAIT_SCHEDULE.getStatus());
            } else {
                pipelineRecord.setExecStatus(PipelineStatus.WAIT_RUNNABLE.getStatus());
            }
            pipelineRecord.setAuditUserId(user.getId());
            pipelineRecord.setAuditUserName(user.getUsername());
            pipelineRecord.setAuditTime(new Date());
            if (isAdmin) {
                pipelineRecord.setAuditReason(MessageConst.AUTO_AUDIT_RESOLVE);
            } else {
                pipelineRecord.setAuditReason(MessageConst.AUDIT_NOT_REQUIRED);
            }
        }
    }

    /**
     * 设置操作日志参数
     *
     * @param record record
     */
    private void setEventLogParams(ApplicationPipelineRecordDO record) {
        EventParamsHolder.addParam(EventKeys.ID, record.getId());
        EventParamsHolder.addParam(EventKeys.PIPELINE_ID, record.getPipelineId());
        EventParamsHolder.addParam(EventKeys.NAME, record.getPipelineName());
        EventParamsHolder.addParam(EventKeys.TITLE, record.getExecTitle());
    }

}
