package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.PipelineDetailStatus;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.consts.app.TimedType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.ApplicationPipelineStageConfigDTO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.ApplicationPipelineDetailRecordRequest;
import com.orion.ops.entity.request.ApplicationPipelineRecordRequest;
import com.orion.ops.entity.vo.ApplicationPipelineRecordListVO;
import com.orion.ops.service.api.ApplicationBuildService;
import com.orion.ops.service.api.ApplicationPipelineDetailRecordService;
import com.orion.ops.service.api.ApplicationPipelineDetailService;
import com.orion.ops.service.api.ApplicationPipelineRecordService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        return recordId;
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
            pipelineRecord.setExecStatus(ReleaseStatus.WAIT_AUDIT.getStatus());
        } else {
            if (TimedType.TIMED.getType().equals(pipelineRecord.getTimedExec())) {
                pipelineRecord.setExecStatus(ReleaseStatus.WAIT_SCHEDULE.getStatus());
            } else {
                pipelineRecord.setExecStatus(ReleaseStatus.WAIT_RUNNABLE.getStatus());
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

}
