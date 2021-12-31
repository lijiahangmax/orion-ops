package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 文件tail表
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_tail_list")
public class FileTailListDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机器id
     */
    @TableField("machine_id")
    private Long machineId;

    /**
     * 别名
     */
    @TableField("alias_name")
    private String aliasName;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件编码
     */
    @TableField("file_charset")
    private String fileCharset;

    /**
     * 尾部文件偏移行数
     */
    @TableField("file_offset")
    private Integer fileOffset;

    /**
     * tail 命令
     */
    @TableField("tail_command")
    private String tailCommand;

    /**
     * 是否删除 1未删除 2已删除
     *
     * @see com.orion.ops.consts.Const#NOT_DELETED
     * @see com.orion.ops.consts.Const#IS_DELETED
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
