package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件tail表
 *
 * @author Jiahang Li
 * @since 2021-07-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "文件tail表")
@TableName("file_tail_list")
public class FileTailListDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Long machineId;

    @ApiModelProperty(value = "别名")
    @TableField("alias_name")
    private String aliasName;

    @ApiModelProperty(value = "文件路径")
    @TableField("file_path")
    private String filePath;

    @ApiModelProperty(value = "文件编码")
    @TableField("file_charset")
    private String fileCharset;

    @ApiModelProperty(value = "尾部文件偏移行数")
    @TableField("file_offset")
    private Integer fileOffset;

    @ApiModelProperty(value = "tail 命令")
    @TableField("tail_command")
    private String tailCommand;

    /**
     * @see com.orion.ops.constant.tail.FileTailMode
     */
    @ApiModelProperty(value = "宿主机文件追踪类型 tracker/tail")
    @TableField("tail_mode")
    private String tailMode;

    /**
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
