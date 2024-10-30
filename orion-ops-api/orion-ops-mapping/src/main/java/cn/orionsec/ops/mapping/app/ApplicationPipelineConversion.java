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
package cn.orionsec.ops.mapping.app;

import cn.orionsec.ops.entity.domain.ApplicationPipelineDO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineDetailDO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineDetailVO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineStatisticsDetailVO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineVO;
import com.orion.lang.utils.convert.TypeStore;

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
