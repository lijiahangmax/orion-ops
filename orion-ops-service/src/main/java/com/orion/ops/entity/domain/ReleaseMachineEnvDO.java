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
 * 上线单机器环境变量
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("release_machine_env")
public class ReleaseMachineEnvDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 上线单id
     */
    @TableField("release_id")
    private Long releaseId;

    /**
     * 机器id
     */
    @TableField("machine_id")
    private Long machineId;

    /**
     * key
     */
    @TableField("env_key")
    private String envKey;

    /**
     * value
     */
    @TableField("env_value")
    private String envValue;

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
