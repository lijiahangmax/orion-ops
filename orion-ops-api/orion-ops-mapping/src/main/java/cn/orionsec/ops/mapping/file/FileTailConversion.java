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
package cn.orionsec.ops.mapping.file;

import cn.orionsec.ops.entity.domain.FileTailListDO;
import cn.orionsec.ops.entity.dto.file.FileTailDTO;
import cn.orionsec.ops.entity.vo.tail.FileTailVO;
import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.time.Dates;

/**
 * 文件 tail 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/10 18:53
 */
public class FileTailConversion {

    static {
        TypeStore.STORE.register(FileTailVO.class, FileTailDTO.class, p -> {
            FileTailDTO dto = new FileTailDTO();
            dto.setMachineId(p.getMachineId());
            dto.setFilePath(p.getPath());
            dto.setOffset(p.getOffset());
            dto.setCharset(p.getCharset());
            dto.setCommand(p.getCommand());
            return dto;
        });
    }

    static {
        TypeStore.STORE.register(FileTailListDO.class, FileTailVO.class, p -> {
            FileTailVO vo = new FileTailVO();
            vo.setId(p.getId());
            vo.setName(p.getAliasName());
            vo.setMachineId(p.getMachineId());
            vo.setPath(p.getFilePath());
            vo.setFileName(Files1.getFileName(p.getFilePath()));
            vo.setOffset(p.getFileOffset());
            vo.setCharset(p.getFileCharset());
            vo.setCommand(p.getTailCommand());
            vo.setTailMode(p.getTailMode());
            vo.setUpdateTime(p.getUpdateTime());
            vo.setUpdateTimeAgo(Dates.ago(p.getUpdateTime()));
            return vo;
        });
    }
}
