package com.orion.ops.mapping.app;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationPipelineDO;
import com.orion.ops.entity.domain.ApplicationPipelineDetailDO;
import com.orion.ops.entity.vo.app.ApplicationPipelineDetailVO;
import com.orion.ops.entity.vo.app.ApplicationPipelineStatisticsDetailVO;
import com.orion.ops.entity.vo.app.ApplicationPipelineVO;

/**
 * 应用流水线 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:43
 */
public class ApplicationPipelineConversion {

    static {
        TypeStore.STORE.register(ApplicationPipelineDO.class, ApplicationPipelineVO.class, p -> {
            ApplicationPipelineVO vo = new ApplicationPipelineVO();
            vo.setId(p.getId());
            vo.setName(p.getPipelineName());
            vo.setDescription(p.getDescription());
            vo.setProfileId(p.getProfileId());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            return vo;
        });
    }

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
