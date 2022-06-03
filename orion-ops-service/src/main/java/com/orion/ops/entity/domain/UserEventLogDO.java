package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户事件日志
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_event_log")
public class UserEventLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 事件分类
     *
     * @see com.orion.ops.consts.event.EventClassify
     */
    @TableField("event_classify")
    private Integer eventClassify;

    /**
     * 事件类型
     *
     * @see com.orion.ops.consts.event.EventType
     */
    @TableField("event_type")
    private Integer eventType;

    /**
     * 日志信息
     */
    @TableField("log_info")
    private String logInfo;

    /**
     * 日志参数
     */
    @TableField("params_json")
    private String paramsJson;

    /**
     * 是否执行成功 1成功 2失败
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    @TableField("exec_result")
    private Integer execResult;

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

}
