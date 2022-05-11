package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineDetailDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 应用流水线详情
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:40
 */
@Data
public class ApplicationPipelineStatisticsDetailVO {

    /**
     * id
     */
    private Long id;

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
     *
     * @see com.orion.ops.consts.app.StageType
     */
    private Integer stageType;

    /**
     * 平均操作时长ms (成功)
     */
    private Long avgUsed;

    /**
     * 平均操作时长 (成功)
     */
    private String avgUsedInterval;

    static {
        TypeStore.STORE.register(ApplicationPipelineDetailDO.class, ApplicationPipelineStatisticsDetailVO.class, p -> {
            ApplicationPipelineStatisticsDetailVO vo = new ApplicationPipelineStatisticsDetailVO();
            vo.setId(p.getId());
            vo.setAppId(p.getAppId());
            vo.setStageType(p.getStageType());
            return vo;
        });
    }

}
