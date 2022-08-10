package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 机器终端配置表
 *
 * @author Jiahang Li
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "机器终端配置表")
@TableName("machine_terminal")
@SuppressWarnings("ALL")
public class MachineTerminalDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Long machineId;

    /**
     * @see com.orion.net.remote.TerminalType#XTERM
     */
    @ApiModelProperty(value = "终端类型")
    @TableField("terminal_type")
    private String terminalType;

    /**
     * @see com.orion.ops.constant.terminal.TerminalConst#BACKGROUND_COLOR
     */
    @ApiModelProperty(value = "背景色")
    @TableField("background_color")
    private String backgroundColor;

    /**
     * @see com.orion.ops.constant.terminal.TerminalConst#FONT_COLOR
     */
    @ApiModelProperty(value = "字体颜色")
    @TableField("font_color")
    private String fontColor;

    /**
     * @see com.orion.ops.constant.terminal.TerminalConst#FONT_SIZE
     */
    @ApiModelProperty(value = "字体大小")
    @TableField("font_size")
    private Integer fontSize;

    /**
     * @see com.orion.ops.constant.terminal.TerminalConst#FONT_FAMILY
     */
    @ApiModelProperty(value = "字体名称")
    @TableField("font_family")
    private String fontFamily;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "是否开启url link 1开启 2关闭")
    @TableField("enable_web_link")
    private Integer enableWebLink;

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
