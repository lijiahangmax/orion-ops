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
 * 机房表
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-04-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("machine_room")
public class MachineRoomDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机房名称
     */
    @TableField("room_name")
    private String roomName;

    /**
     * 标识
     */
    @TableField("room_tag")
    private String roomTag;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态 1正常 2关闭
     */
    @TableField("room_status")
    private Integer roomStatus;

    /**
     * 负责人id
     */
    @TableField("manager_id")
    private Long managerId;

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
