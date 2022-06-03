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
     * 环境id
     */
    private Long profileId;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用唯一标识
     */
    private String appTag;

    /**
     * 版本控制id
     */
    private Long vcsId;

    /**
     * 阶段类型 10构建 20发布
     *
     * @see com.orion.ops.consts.app.StageType
     */
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
