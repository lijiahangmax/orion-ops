package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 机器终端配置表
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("machine_terminal")
public class MachineTerminalDO implements Serializable {

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
     * 终端类型
     *
     * @see com.orion.net.remote.TerminalType#XTERM
     */
    @TableField("terminal_type")
    private String terminalType;

    /**
     * 背景色
     *
     * @see com.orion.ops.consts.terminal.TerminalConst#BACKGROUND_COLOR
     */
    @TableField("background_color")
    private String backgroundColor;

    /**
     * 字体颜色
     *
     * @see com.orion.ops.consts.terminal.TerminalConst#FONT_COLOR
     */
    @TableField("font_color")
    private String fontColor;

    /**
     * 字体大小
     *
     * @see com.orion.ops.consts.terminal.TerminalConst#FONT_SIZE
     */
    @TableField("font_size")
    private Integer fontSize;

    /**
     * 字体名称
     *
     * @see com.orion.ops.consts.terminal.TerminalConst#FONT_FAMILY
     */
    @TableField("font_family")
    private String fontFamily;

    /**
     * 是否开启url link 1开启 2关闭
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    @TableField("enable_web_link")
    private Integer enableWebLink;

    /**
     * 是否开启webGL加速 1开启 2关闭
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    @TableField("enable_web_gl")
    private Integer enableWebGL;

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
