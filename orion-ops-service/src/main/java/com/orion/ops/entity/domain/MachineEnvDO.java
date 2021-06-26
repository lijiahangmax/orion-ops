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
 * 机器环境变量
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("machine_env")
public class MachineEnvDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机器id  宿主机为1
     */
    @TableField("machine_id")
    private Long machineId;

    /**
     * key
     */
    @TableField("attr_key")
    private String attrKey;

    /**
     * value
     */
    @TableField("attr_value")
    private String attrValue;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否禁止删除 1可以删除 2禁止删除
     *
     * @see com.orion.ops.consts.Const#FORBID_DELETE_CAN
     * @see com.orion.ops.consts.Const#FORBID_DELETE_NOT
     */
    @TableField("forbid_delete")
    private Integer forbidDelete;

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
