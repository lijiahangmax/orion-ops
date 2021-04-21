package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
     */
    @TableField("terminal_type")
    private String terminalType;

    /**
     * 背景色
     */
    @TableField("background_color")
    private String backgroundColor;

    /**
     * 字体颜色
     */
    @TableField("font_color")
    private String fontColor;

    /**
     * 字体大小
     */
    @TableField("font_size")
    private Integer fontSize;

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
