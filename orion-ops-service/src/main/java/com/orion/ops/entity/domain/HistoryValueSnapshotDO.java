package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.orion.ops.consts.history.HistoryOperator;
import com.orion.ops.consts.history.HistoryValueType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 历史值快照表
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("history_value_snapshot")
public class HistoryValueSnapshotDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 值id
     */
    @TableField("value_id")
    private Long valueId;

    /**
     * 操作类型 1新增 2修改 3删除
     *
     * @see HistoryOperator
     */
    @TableField("operator_type")
    private Integer operatorType;

    /**
     * 值类型 10机器环境变量 20应用环境变量
     *
     * @see HistoryValueType
     */
    @TableField("value_type")
    private Integer valueType;

    /**
     * 原始值
     */
    @TableField("before_value")
    private String beforeValue;

    /**
     * 新值
     */
    @TableField("after_value")
    private String afterValue;

    /**
     * 修改人id
     */
    @TableField("update_user_id")
    private Long updateUserId;

    /**
     * 修改人用户名
     */
    @TableField("update_user_name")
    private String updateUserName;

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
