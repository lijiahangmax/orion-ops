package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationPipelineDetailDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用流水线详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:30
 */
@Data
@ApiModel(value = "应用流水线详情响应")
public class ApplicationPipelineDetailVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水线id")
    private Long pipelineId;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用唯一标识")
    private String appTag;

    @ApiModelProperty(value = "应用版本仓库id")
    private Long repoId;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer stageType;

    static {
        TypeStore.STORE.register(ApplicationPipelineDetailDO.class, ApplicationPipelineDetailVO.class, p -> {
            ApplicationPipelineDetailVO vo = new ApplicationPipelineDetailVO();
            vo.setId(p.getId());
            vo.setPipelineId(p.getPipelineId());
            vo.setAppId(p.getAppId());
            vo.setProfileId(p.getProfileId());
            vo.setStageType(p.getStageType());
            return vo;
        });
    }

}
