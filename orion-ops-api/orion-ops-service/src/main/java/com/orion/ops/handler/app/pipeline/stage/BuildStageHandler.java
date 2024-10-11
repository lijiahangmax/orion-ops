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
package com.orion.ops.handler.app.pipeline.stage;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Exceptions;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.app.BuildStatus;
import com.orion.ops.constant.app.PipelineLogStatus;
import com.orion.ops.constant.app.StageType;
import com.orion.ops.dao.ApplicationBuildDAO;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.ops.entity.dto.app.ApplicationPipelineStageConfigDTO;
import com.orion.ops.entity.request.app.ApplicationBuildRequest;
import com.orion.ops.handler.app.build.BuildSessionHolder;
import com.orion.ops.handler.app.machine.BuildMachineProcessor;
import com.orion.ops.handler.app.machine.IMachineProcessor;
import com.orion.ops.service.api.ApplicationBuildService;
import com.orion.spring.SpringHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * 构建阶段操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/15 15:35
 */
@Slf4j
public class BuildStageHandler extends AbstractStageHandler {

    private Long buildId;

    private static final ApplicationBuildService applicationBuildService = SpringHolder.getBean(ApplicationBuildService.class);

    private static final ApplicationBuildDAO applicationBuildDAO  = SpringHolder.getBean(ApplicationBuildDAO.class);

    private static final BuildSessionHolder buildSessionHolder = SpringHolder.getBean(BuildSessionHolder.class);

    public BuildStageHandler(ApplicationPipelineTaskDO task, ApplicationPipelineTaskDetailDO detail) {
        super(task, detail);
        this.stageType = StageType.BUILD;
    }

    @Override
    protected void execStageTask() {
        // 设置用户上下文
        this.setExecuteUserContext();
        // 参数
        ApplicationBuildRequest request = new ApplicationBuildRequest();
        request.setAppId(detail.getAppId());
        request.setProfileId(task.getProfileId());
        // 配置
        ApplicationPipelineStageConfigDTO config = JSON.parseObject(detail.getStageConfig(), ApplicationPipelineStageConfigDTO.class);
        request.setBranchName(config.getBranchName());
        request.setCommitId(config.getCommitId());
        request.setDescription(config.getDescription());
        log.info("执行流水线任务-构建阶段-开始创建 detailId: {}, 参数: {}", detailId, JSON.toJSONString(request));
        // 创建构建任务
        this.buildId = applicationBuildService.submitBuildTask(request, false);
        // 设置构建id
        this.setRelId(buildId);
        // 插入创建日志
        ApplicationBuildDO build = applicationBuildDAO.selectById(buildId);
        this.addLog(PipelineLogStatus.CREATE, detail.getAppName(), build.getBuildSeq());
        log.info("执行流水线任务-构建阶段-创建完成开始执行 detailId: {}, buildId: {}", detailId, buildId);
        // 插入执行日志
        this.addLog(PipelineLogStatus.EXEC, detail.getAppName());
        // 执行构建任务
        new BuildMachineProcessor(buildId).run();
        // 检查执行结果
        build = applicationBuildDAO.selectById(buildId);
        if (BuildStatus.FAILURE.getStatus().equals(build.getBuildStatus())) {
            // 异常抛出
            throw Exceptions.runtime(MessageConst.OPERATOR_ERROR);
        } else if (BuildStatus.TERMINATED.getStatus().equals(build.getBuildStatus())) {
            this.terminated = true;
        }
    }

    @Override
    public void terminate() {
        super.terminate();
        // 获取数据
        ApplicationBuildDO build = applicationBuildDAO.selectById(buildId);
        // 检查状态
        if (!BuildStatus.RUNNABLE.getStatus().equals(build.getBuildStatus())) {
            return;
        }
        // 获取实例
        IMachineProcessor session = buildSessionHolder.getSession(buildId);
        if (session == null) {
            return;
        }
        // 调用终止
        session.terminate();
    }

}
