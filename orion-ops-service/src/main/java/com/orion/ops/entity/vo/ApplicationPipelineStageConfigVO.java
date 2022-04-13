package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.ApplicationPipelineStageConfigDTO;
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
public class ApplicationPipelineStageConfigVO {

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
     * 构建序列
     */
    private Integer buildSeq;

    /**
     * 发布机器id
     */
    private List<Long> machineIdList;

    /**
     * 发布机器
     */
    private List<MachineInfoVO> machineList;

    static {
        TypeStore.STORE.register(ApplicationPipelineStageConfigDTO.class, ApplicationPipelineStageConfigVO.class, p -> {
            ApplicationPipelineStageConfigVO dto = new ApplicationPipelineStageConfigVO();
            dto.setBranchName(p.getBranchName());
            dto.setCommitId(p.getCommitId());
            dto.setBuildId(p.getBuildId());
            dto.setMachineIdList(p.getMachineIdList());
            return dto;
        });
    }

}
