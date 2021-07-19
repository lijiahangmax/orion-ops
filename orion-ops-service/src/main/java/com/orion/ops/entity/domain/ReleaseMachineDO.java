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
 *
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("release_machine")
public class ReleaseMachineDO implements Serializable {

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
     * 机器名称
     */
    @TableField("machine_name")
    private String machineName;

    /**
     * 机器tag
     */
    @TableField("machine_tag")
    private String machineTag;

    /**
     * 机器主机
     */
    @TableField("machine_host")
    private String machineHost;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过
     *
     * @see com.orion.ops.consts.app.ActionStatus
     */
    @TableField("run_status")
    private Integer runStatus;

    /**
     * 日志路径
     */
    @TableField("log_path")
    private String logPath;

    /**
     * 产物文件
     */
    @TableField("dist_path")
    private String distPath;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private Date endTime;

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
