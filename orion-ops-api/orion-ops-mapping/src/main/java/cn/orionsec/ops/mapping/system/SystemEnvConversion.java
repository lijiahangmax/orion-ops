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
package cn.orionsec.ops.mapping.system;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.entity.domain.SystemEnvDO;
import cn.orionsec.ops.entity.vo.system.SystemEnvVO;
import com.orion.lang.utils.convert.TypeStore;

/**
 * 系统环境变量 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:23
 */
public class SystemEnvConversion {

    static {
        TypeStore.STORE.register(SystemEnvDO.class, SystemEnvVO.class, p -> {
            SystemEnvVO vo = new SystemEnvVO();
            vo.setId(p.getId());
            vo.setKey(p.getAttrKey());
            vo.setValue(p.getAttrValue());
            vo.setDescription(p.getDescription());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            Integer forbidDelete = SystemEnvAttr.of(p.getAttrKey()) == null ? Const.FORBID_DELETE_CAN : Const.FORBID_DELETE_NOT;
            vo.setForbidDelete(forbidDelete);
            return vo;
        });
    }

}
