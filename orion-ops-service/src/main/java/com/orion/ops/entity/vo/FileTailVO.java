package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.ops.entity.dto.FileTailDTO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 文件tail 返回
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/20 19:17
 */
@Data
public class FileTailVO {

    /**
     * token
     */
    private String token;

    /**
     * id
     */
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 机器host
     */
    private String machineHost;

    /**
     * 文件
     */
    private String path;

    /**
     * offset
     *
     * @see com.orion.ops.consts.Const#TAIL_OFFSET_LINE
     */
    private Integer offset;

    /**
     * 编码集
     *
     * @see com.orion.ops.consts.Const#UTF_8
     */
    private String charset;

    static {
        TypeStore.STORE.register(FileTailListDO.class, FileTailVO.class, p -> {
            FileTailVO vo = new FileTailVO();
            vo.setId(p.getId());
            vo.setName(p.getAliasName());
            vo.setMachineId(p.getMachineId());
            vo.setPath(p.getFilePath());
            vo.setOffset(p.getFileOffset());
            vo.setCharset(p.getFileCharset());
            return vo;
        });

        TypeStore.STORE.register(FileTailVO.class, FileTailDTO.class, p -> {
            FileTailDTO dto = new FileTailDTO();
            dto.setMachineId(p.getMachineId());
            dto.setFilePath(p.getPath());
            dto.setOffset(p.getOffset());
            dto.setCharset(p.getCharset());
            return dto;
        });
    }

}
