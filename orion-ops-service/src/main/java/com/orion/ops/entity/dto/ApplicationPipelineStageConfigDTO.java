package com.orion.ops.entity.dto;

import com.orion.ops.entity.request.ApplicationPipelineTaskDetailRequest;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.List;

/**
 * 应用操作流水线配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/8 10:00
 */
@Data
public class ApplicationPipelineStageConfigDTO {

    /**
     * 分支名称
     */
    private String branchName;

    /**
     * 提交id
     */
    private String commitId;

    /**
     * 构建id
     */
    private Long buildId;

    /**
     * 发布标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 发布机器id
     */
    private List<Long> machineIdList;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDetailRequest.class, ApplicationPipelineStageConfigDTO.class, p -> {
            ApplicationPipelineStageConfigDTO dto = new ApplicationPipelineStageConfigDTO();
            dto.setBranchName(p.getBranchName());
            dto.setCommitId(p.getCommitId());
            dto.setBuildId(p.getBuildId());
            dto.setTitle(p.getTitle());
            dto.setDescription(p.getDescription());
            dto.setMachineIdList(p.getMachineIdList());
            return dto;
        });

        TypeStore.STORE.register(ApplicationPipelineStageConfigDTO.class, ApplicationPipelineTaskDetailRequest.class, p -> {
            ApplicationPipelineTaskDetailRequest req = new ApplicationPipelineTaskDetailRequest();
            req.setBranchName(p.getBranchName());
            req.setCommitId(p.getCommitId());
            req.setBuildId(p.getBuildId());
            req.setTitle(p.getTitle());
            req.setDescription(p.getDescription());
            req.setMachineIdList(p.getMachineIdList());
            return req;
        });
    }

}
