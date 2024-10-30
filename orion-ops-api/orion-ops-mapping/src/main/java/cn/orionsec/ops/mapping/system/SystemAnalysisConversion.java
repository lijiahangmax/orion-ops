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

import cn.orionsec.ops.entity.dto.system.SystemSpaceAnalysisDTO;
import cn.orionsec.ops.entity.vo.system.SystemAnalysisVO;
import com.orion.lang.utils.convert.TypeStore;

/**
 * 系统统计分析 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:22
 */
public class SystemAnalysisConversion {

    static {
        TypeStore.STORE.register(SystemSpaceAnalysisDTO.class, SystemAnalysisVO.class, p -> {
            SystemAnalysisVO vo = new SystemAnalysisVO();
            vo.setTempFileCount(p.getTempFileCount());
            vo.setTempFileSize(p.getTempFileSize());
            vo.setLogFileCount(p.getLogFileCount());
            vo.setLogFileSize(p.getLogFileSize());
            vo.setSwapFileCount(p.getSwapFileCount());
            vo.setSwapFileSize(p.getSwapFileSize());
            vo.setDistVersionCount(p.getDistVersionCount());
            vo.setDistFileSize(p.getDistFileSize());
            vo.setRepoVersionCount(p.getRepoVersionCount());
            vo.setRepoVersionFileSize(p.getRepoVersionFileSize());
            vo.setScreenFileCount(p.getScreenFileCount());
            vo.setScreenFileSize(p.getScreenFileSize());
            return vo;
        });
    }

}
