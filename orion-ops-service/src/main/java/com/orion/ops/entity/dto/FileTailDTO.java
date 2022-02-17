package com.orion.ops.entity.dto;

import com.orion.ops.entity.vo.FileTailVO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 文件tail 对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/10 18:53
 */
@Data
public class FileTailDTO {

    /**
     * 文件绝对路径
     */
    private String filePath;

    /**
     * 下载用户id
     */
    private Long userId;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * tail 模式
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#TAIL_MODE
     * @see com.orion.ops.consts.tail.FileTailMode
     */
    private String mode;

    /**
     * tail 尾行偏移量
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#TAIL_OFFSET
     * @see com.orion.ops.consts.Const#TAIL_OFFSET_LINE
     */
    private Integer offset;

    /**
     * 编码集
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#TAIL_CHARSET
     * @see com.orion.ops.consts.Const#UTF_8
     */
    private String charset;

    /**
     * tail 命令
     */
    private String command;

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

}
