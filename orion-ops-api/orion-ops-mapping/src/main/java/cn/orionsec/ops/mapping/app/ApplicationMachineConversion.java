/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
import cn.orionsec.ops.entity.domain.ApplicationMachineDO;
import cn.orionsec.ops.entity.vo.app.ApplicationMachineVO;

/**
 * 应用机器 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:41
 */
public class ApplicationMachineConversion {

    static {
        TypeStore.STORE.register(ApplicationMachineDO.class, ApplicationMachineVO.class, p -> {
            ApplicationMachineVO vo = new ApplicationMachineVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setReleaseId(p.getReleaseId());
            vo.setBuildId(p.getBuildId());
            vo.setBuildSeq(p.getBuildSeq());
            return vo;
        });
    }

}
