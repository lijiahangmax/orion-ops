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
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.app.ApplicationEnvAttr;
import cn.orionsec.ops.entity.domain.ApplicationEnvDO;
import cn.orionsec.ops.entity.vo.app.ApplicationEnvVO;

/**
 * 应用环境变量 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:40
 */
public class ApplicationEnvConversion {

    static {
        TypeStore.STORE.register(ApplicationEnvDO.class, ApplicationEnvVO.class, p -> {
            ApplicationEnvVO vo = new ApplicationEnvVO();
            vo.setId(p.getId());
            vo.setAppId(p.getAppId());
            vo.setProfileId(p.getProfileId());
            vo.setKey(p.getAttrKey());
            vo.setValue(p.getAttrValue());
            vo.setDescription(p.getDescription());
            vo.setUpdateTime(p.getUpdateTime());
            Integer forbidDelete = ApplicationEnvAttr.of(p.getAttrKey()) == null ? Const.FORBID_DELETE_CAN : Const.FORBID_DELETE_NOT;
            vo.setForbidDelete(forbidDelete);
            return vo;
        });
    }

}
