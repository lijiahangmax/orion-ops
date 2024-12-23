/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.handler.app.pipeline.stage;

import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.CnConst;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.app.PipelineLogStatus;
import cn.orionsec.ops.constant.app.ReleaseStatus;
import cn.orionsec.ops.constant.app.StageType;
import cn.orionsec.ops.dao.ApplicationBuildDAO;
import cn.orionsec.ops.dao.ApplicationReleaseDAO;
import cn.orionsec.ops.entity.domain.*;
import cn.orionsec.ops.entity.dto.app.ApplicationPipelineStageConfigDTO;
import cn.orionsec.ops.entity.request.app.ApplicationReleaseRequest;
import cn.orionsec.ops.handler.app.release.IReleaseProcessor;
import cn.orionsec.ops.handler.app.release.ReleaseSessionHolder;
import cn.orionsec.ops.service.api.ApplicationMachineService;
import cn.orionsec.ops.service.api.ApplicationReleaseService;
import com.alibaba.fastjson.JSON;
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

    private static final ApplicationBuildDAO applicationBuildDAO = SpringHolder.getBean(ApplicationBuildDAO.class);

    private static final ApplicationReleaseDAO applicationReleaseDAO = SpringHolder.getBean(ApplicationReleaseDAO.class);

    private static final ApplicationReleaseService applicationReleaseService = SpringHolder.getBean(ApplicationReleaseService.class);

    private static final ApplicationMachineService applicationMachineService = SpringHolder.getBean(ApplicationMachineService.class);

    private static final ReleaseSessionHolder releaseSessionHolder = SpringHolder.getBean(ReleaseSessionHolder.class);

    private Long releaseId;

    private ApplicationReleaseDO release;

    public ReleaseStageHandler(ApplicationPipelineTaskDO task, ApplicationPipelineTaskDetailDO detail) {
        super(task, detail);
        this.stageType = StageType.RELEASE;
    }

    @Override
    protected void execStageTask() {
        // 创建
        this.createReleaseTask();
        // 审核
        this.auditReleaseTask();
        // 执行
        this.execReleaseTask();
    }

    /**
     * 创建发布任务
     */
    protected void createReleaseTask() {
        // 设置用户上下文
        this.setExecuteUserContext();
        Long profileId = task.getProfileId();
        Long appId = detail.getAppId();
        // 创建发布任务参数
        ApplicationReleaseRequest request = new ApplicationReleaseRequest();
        request.setProfileId(profileId);
        request.setAppId(appId);
        // 其他配置
        ApplicationPipelineStageConfigDTO config = JSON.parseObject(detail.getStageConfig(), ApplicationPipelineStageConfigDTO.class);
        request.setDescription(config.getDescription());
        // 标题
        request.setTitle(Strings.def(config.getTitle(), () -> CnConst.RELEASE + Const.SPACE + detail.getAppName()));
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
        ApplicationPipelineTaskDetailDO updateConfig = new ApplicationPipelineTaskDetailDO();
        updateConfig.setId(detailId);
        updateConfig.setStageConfig(configJson);
        applicationPipelineTaskDetailDAO.updateById(updateConfig);
        // 创建发布任务
        log.info("执行流水线任务-发布阶段-开始创建 detailId: {}, 参数: {}", detailId, JSON.toJSONString(request));
        this.releaseId = applicationReleaseService.submitAppRelease(request);
        // 设置发布id
        this.setRelId(releaseId);
        // 插入日志
        this.addLog(PipelineLogStatus.CREATE, detail.getAppName(), build.getBuildSeq());
    }

    /**
     * 审核发布任务
     */
    private void auditReleaseTask() {
        // 查询发布任务
        this.release = applicationReleaseDAO.selectById(releaseId);
        if (!ReleaseStatus.WAIT_AUDIT.getStatus().equals(release.getReleaseStatus())) {
            return;
        }
        ApplicationReleaseDO update = new ApplicationReleaseDO();
        update.setId(releaseId);
        update.setAuditUserId(task.getAuditUserId());
        update.setAuditUserName(task.getAuditUserName());
        update.setAuditReason(MessageConst.AUTO_AUDIT_RESOLVE);
        update.setAuditTime(new Date());
        log.info("执行流水线任务-发布阶段-审核 detailId: {}, releaseId: {}, 参数: {}", detailId, releaseId, JSON.toJSONString(update));
        applicationReleaseDAO.updateById(update);
    }

    /**
     * 执行发布任务
     */
    private void execReleaseTask() {
        log.info("执行流水线任务-发布阶段-开始执行 detailId: {}, releaseId: {}", detailId, releaseId);
        // 提交发布任务
        applicationReleaseService.runnableAppRelease(releaseId, true, false);
        // 插入执行日志
        this.addLog(PipelineLogStatus.EXEC, detail.getAppName());
        // 执行发布任务
        IReleaseProcessor.with(release).run();
        // 检查执行结果
        this.release = applicationReleaseDAO.selectById(releaseId);
        if (ReleaseStatus.FAILURE.getStatus().equals(release.getReleaseStatus())) {
            // 异常抛出
            throw Exceptions.runtime(MessageConst.OPERATOR_ERROR);
        } else if (ReleaseStatus.TERMINATED.getStatus().equals(release.getReleaseStatus())) {
            // 停止
            this.terminated = true;
        }
    }

    @Override
    public void terminate() {
        super.terminate();
        // 获取数据
        this.release = applicationReleaseDAO.selectById(releaseId);
        // 检查状态
        if (!ReleaseStatus.RUNNABLE.getStatus().equals(release.getReleaseStatus())) {
            return;
        }
        // 获取实例
        IReleaseProcessor session = releaseSessionHolder.getSession(releaseId);
        if (session == null) {
            return;
        }
        // 调用终止
        session.terminateAll();
    }

}

