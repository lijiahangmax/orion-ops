package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineDetailDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 应用流水线详情
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:30
 */
@Data
public class ApplicationPipelineDetailVO {

    /**
     * id
     */
    private Long id;

    /**
     * 流水线id
     */
    private Long pipelineId;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 阶段类型 10构建 20发布
     */
    private Integer stageType;

    static {
        TypeStore.STORE.register(ApplicationPipelineDetailDO.class, ApplicationPipelineDetailVO.class, p -> {
            ApplicationPipelineDetailVO vo = new ApplicationPipelineDetailVO();
            vo.setId(p.getId());
            vo.setPipelineId(p.getPipelineId());
            vo.setAppId(p.getAppId());
            vo.setStageType(p.getStageType());
            return vo;
        });
    }

}
