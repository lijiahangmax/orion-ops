package com.orion.ops.entity.dto;

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
     * @see com.orion.ops.consts.machine.MachineEnvAttr#TAIL_MODE
     * @see com.orion.ops.consts.tail.FileTailMode
     */
    private String mode;

    /**
     * tail 尾行偏移量
     *
     * @see com.orion.ops.consts.machine.MachineEnvAttr#TAIL_OFFSET
     * @see com.orion.ops.consts.Const#TAIL_OFFSET_LINE
     */
    private Integer offset;

    /**
     * 编码集
     *
     * @see com.orion.ops.consts.machine.MachineEnvAttr#TAIL_CHARSET
     */
    private String charset;

}
