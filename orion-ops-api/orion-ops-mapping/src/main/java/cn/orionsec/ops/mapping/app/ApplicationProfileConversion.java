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

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.ops.entity.domain.ApplicationProfileDO;
import cn.orionsec.ops.entity.dto.app.ApplicationProfileDTO;
import cn.orionsec.ops.entity.vo.app.ApplicationProfileFastVO;
import cn.orionsec.ops.entity.vo.app.ApplicationProfileVO;

/**
 * 应用环境 对象转换器
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
public class ApplicationProfileConversion {

    static {
        TypeStore.STORE.register(ApplicationProfileDO.class, ApplicationProfileVO.class, p -> {
            ApplicationProfileVO vo = new ApplicationProfileVO();
            vo.setId(p.getId());
            vo.setName(p.getProfileName());
            vo.setTag(p.getProfileTag());
            vo.setDescription(p.getDescription());
            vo.setReleaseAudit(p.getReleaseAudit());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationProfileDO.class, ApplicationProfileDTO.class, p -> {
            ApplicationProfileDTO dto = new ApplicationProfileDTO();
            dto.setId(p.getId());
            dto.setProfileName(p.getProfileName());
            dto.setProfileTag(p.getProfileTag());
            return dto;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationProfileDTO.class, ApplicationProfileFastVO.class, p -> {
            ApplicationProfileFastVO vo = new ApplicationProfileFastVO();
            vo.setId(p.getId());
            vo.setName(p.getProfileName());
            vo.setTag(p.getProfileTag());
            return vo;
        });
    }

}
