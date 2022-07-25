package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.dto.ApplicationPipelineStageConfigDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用操作流水线配置响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/8 10:00
 */
@Data
@ApiModel(value = "应用操作流水线配置响应")
public class ApplicationPipelineStageConfigVO {

    @ApiModelProperty(value = "分支名称")
    private String branchName;

    @ApiModelProperty(value = "提交id")
    private String commitId;

    @ApiModelProperty(value = "构建id")
    private Long buildId;

    @ApiModelProperty(value = "构建序列")
    private Integer buildSeq;

    @ApiModelProperty(value = "发布机器id")
    private List<Long> machineIdList;

    @ApiModelProperty(value = "发布机器")
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
