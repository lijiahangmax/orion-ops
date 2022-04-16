package com.orion.ops.handler.app.pipeline.stage;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.PipelineLogStatus;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.dao.ApplicationBuildDAO;
import com.orion.ops.dao.ApplicationReleaseDAO;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.ApplicationPipelineStageConfigDTO;
import com.orion.ops.entity.request.ApplicationReleaseRequest;
import com.orion.ops.handler.app.release.IReleaseProcessor;
import com.orion.ops.service.api.ApplicationMachineService;
import com.orion.ops.service.api.ApplicationReleaseService;
import com.orion.ops.utils.Valid;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 发布阶段操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/15 15:38
 */
@Slf4j
public class ReleaseStageHandler extends AbstractStageHandler {

    private static ApplicationBuildDAO applicationBuildDAO = SpringHolder.getBean(ApplicationBuildDAO.class);

    private static ApplicationReleaseDAO applicationReleaseDAO = SpringHolder.getBean(ApplicationReleaseDAO.class);

    private static ApplicationReleaseService applicationReleaseService = SpringHolder.getBean(ApplicationReleaseService.class);

    private static ApplicationMachineService applicationMachineService = SpringHolder.getBean(ApplicationMachineService.class);

    private ApplicationReleaseDO release;

    public ReleaseStageHandler(ApplicationPipelineRecordDO record, ApplicationPipelineDetailRecordDO detail) {
        super(record, detail);
        this.stageType = StageType.RELEASE;
    }

    @Override
    protected void execStageTask() {
        // 创建
        Long releaseId = this.createStageTask();
        // 审核
        this.auditStageTask(releaseId);
        // 执行
        this.execStageTask(releaseId);
    }

    /**
     * 创建发布任务
     *
     * @return releaseId
     */
    protected Long createStageTask() {
        // 设置用户上下文
        this.setExecuteUserContext();
        Long profileId = record.getProfileId();
        Long appId = detail.getAppId();
        // 创建发布任务参数
        ApplicationReleaseRequest request = new ApplicationReleaseRequest();
        request.setProfileId(profileId);
        request.setAppId(appId);
        // 其他配置
        ApplicationPipelineStageConfigDTO config = JSON.parseObject(detail.getStageConfig(), ApplicationPipelineStageConfigDTO.class);
        request.setDescription(config.getDescription());
        // 标题
        request.setTitle(Strings.def(config.getTitle(), () -> Const.RELEASE_CN + Const.SPACE + detail.getAppName()));
        // 构建id
        Long buildId = config.getBuildId();
        ApplicationBuildDO build = null;
        if (buildId == null) {
            // 最新版本
            List<ApplicationBuildDO> buildList = applicationBuildDAO.selectBuildReleaseList(appId, profileId, 1);
            if (!buildList.isEmpty()) {
                build = buildList.get(0);
                buildId = build.getId();
            }
        } else {
            build = applicationBuildDAO.selectById(buildId);
        }
        if (build == null) {
            throw Exceptions.argument(Strings.format(MessageConst.APP_LAST_BUILD_VERSION_ABSENT, detail.getAppName()));
        }
        request.setBuildId(buildId);
        config.setBuildId(buildId);
        // 发布机器
        List<Long> machineIdList = config.getMachineIdList();
        if (Lists.isEmpty(machineIdList)) {
            // 全部机器
            machineIdList = applicationMachineService.getAppProfileMachineList(appId, profileId).stream()
                    .map(ApplicationMachineDO::getMachineId)
                    .collect(Collectors.toList());
        }
        request.setMachineIdList(machineIdList);
        config.setMachineIdList(machineIdList);
        // 更新详情配置
        String configJson = JSON.toJSONString(config);
        detail.setStageConfig(configJson);
        ApplicationPipelineDetailRecordDO updateConfig = new ApplicationPipelineDetailRecordDO();
        updateConfig.setId(id);
        updateConfig.setStageConfig(configJson);
        applicationPipelineDetailRecordDAO.updateById(updateConfig);
        // 创建发布任务
        log.info("执行流水线任务-发布阶段-开始创建 detailId: {}, 参数: {}", id, JSON.toJSONString(request));
        Long releaseId = applicationReleaseService.submitAppRelease(request);
        // 插入日志
        this.addLog(PipelineLogStatus.CREATE, detail.getAppName(), build.getBuildSeq());
        // 查询发布任务
        this.release = applicationReleaseDAO.selectById(releaseId);
        return releaseId;
    }

    /**
     * 审核发布任务
     *
     * @param releaseId releaseId
     */
    private void auditStageTask(Long releaseId) {
        if (!ReleaseStatus.WAIT_AUDIT.getStatus().equals(release.getReleaseStatus())) {
            return;
        }
        ApplicationReleaseDO update = new ApplicationReleaseDO();
        update.setId(id);
        update.setAuditUserId(record.getAuditUserId());
        update.setAuditUserName(record.getAuditUserName());
        update.setAuditReason(MessageConst.AUTO_AUDIT_RESOLVE);
        update.setAuditTime(new Date());
        log.info("执行流水线任务-发布阶段-审核 detailId: {}, releaseId: {}, 参数: {}", id, releaseId, JSON.toJSONString(update));
        applicationReleaseDAO.updateById(update);
    }

    /**
     * 执行发布任务
     *
     * @param releaseId releaseId
     */
    private void execStageTask(Long releaseId) {
        log.info("执行流水线任务-发布阶段-开始执行 detailId: {}, releaseId: {}", id, releaseId);
        // 提交发布任务
        applicationReleaseService.runnableAppRelease(releaseId, true, false);
        // 插入执行日志
        this.addLog(PipelineLogStatus.EXEC, detail.getAppName());
        // 执行发布任务
        IReleaseProcessor.with(release).run();
        // 检查执行结果
        this.release = applicationReleaseDAO.selectById(releaseId);
        Valid.isFalse(ReleaseStatus.FAILURE.getStatus().equals(release.getReleaseStatus()), MessageConst.OPERATOR_ERROR);
    }

}

