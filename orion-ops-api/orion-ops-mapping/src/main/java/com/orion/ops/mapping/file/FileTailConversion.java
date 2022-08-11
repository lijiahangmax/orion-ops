package com.orion.ops.mapping.file;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.ops.entity.dto.file.FileTailDTO;
import com.orion.ops.entity.vo.tail.FileTailVO;

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
