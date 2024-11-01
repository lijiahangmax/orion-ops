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
import cn.orionsec.ops.entity.domain.ApplicationInfoDO;
import cn.orionsec.ops.entity.vo.app.ApplicationDetailVO;
import cn.orionsec.ops.entity.vo.app.ApplicationInfoVO;

/**
 * 应用信息 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:39
 */
public class ApplicationConversion {

    static {
        TypeStore.STORE.register(ApplicationInfoDO.class, ApplicationInfoVO.class, p -> {
            ApplicationInfoVO vo = new ApplicationInfoVO();
            vo.setId(p.getId());
            vo.setName(p.getAppName());
            vo.setTag(p.getAppTag());
            vo.setSort(p.getAppSort());
            vo.setRepoId(p.getRepoId());
            vo.setDescription(p.getDescription());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationInfoDO.class, ApplicationDetailVO.class, p -> {
            ApplicationDetailVO vo = new ApplicationDetailVO();
            vo.setId(p.getId());
            vo.setName(p.getAppName());
            vo.setTag(p.getAppTag());
            vo.setDescription(p.getDescription());
            vo.setRepoId(p.getRepoId());
            return vo;
        });
    }

}
