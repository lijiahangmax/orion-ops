package com.orion.ops.handler.app.pipeline.stage;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.BuildStatus;
import com.orion.ops.consts.app.PipelineLogStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.dao.ApplicationBuildDAO;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.entity.domain.ApplicationPipelineDetailRecordDO;
import com.orion.ops.entity.domain.ApplicationPipelineRecordDO;
import com.orion.ops.entity.dto.ApplicationPipelineStageConfigDTO;
import com.orion.ops.entity.request.ApplicationBuildRequest;
import com.orion.ops.handler.app.machine.BuildMachineProcessor;
import com.orion.ops.service.api.ApplicationBuildService;
import com.orion.ops.utils.Valid;
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

    private static ApplicationBuildService applicationBuildService = SpringHolder.getBean(ApplicationBuildService.class);

    private static ApplicationBuildDAO applicationBuildDAO = SpringHolder.getBean(ApplicationBuildDAO.class);

    public BuildStageHandler(ApplicationPipelineRecordDO record, ApplicationPipelineDetailRecordDO detail) {
        super(record, detail);
        this.stageType = StageType.BUILD;
    }

    @Override
    protected void execStageTask() {
        // 设置用户上下文
        this.setExecuteUserContext();
        // 参数
        ApplicationBuildRequest request = new ApplicationBuildRequest();
        request.setAppId(detail.getAppId());
        request.setProfileId(record.getProfileId());
        // 配置
        ApplicationPipelineStageConfigDTO config = JSON.parseObject(detail.getStageConfig(), ApplicationPipelineStageConfigDTO.class);
        request.setBranchName(config.getBranchName());
        request.setCommitId(config.getCommitId());
        request.setDescription(config.getDescription());
        log.info("执行流水线任务-构建阶段-开始创建 detailId: {}, 参数: {}", id, JSON.toJSONString(request));
        // 创建构建任务
        Long buildId = applicationBuildService.submitBuildTask(request, false);
        // 插入创建日志
        ApplicationBuildDO build = applicationBuildDAO.selectById(buildId);
        this.addLog(PipelineLogStatus.CREATE, detail.getAppName(), build.getBuildSeq());
        log.info("执行流水线任务-构建阶段-创建完成开始执行 detailId: {}, id: {}", id, buildId);
        // 插入执行日志
        this.addLog(PipelineLogStatus.EXEC, detail.getAppName());
        // 执行构建任务
        new BuildMachineProcessor(buildId).run();
        // 检查执行结果
        build = applicationBuildDAO.selectById(buildId);
        Valid.isFalse(BuildStatus.FAILURE.getStatus().equals(build.getBuildStatus()), MessageConst.OPERATOR_ERROR);
    }

}
