package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 操作日志请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/10 16:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EventLogRequest extends PageRequest {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 事件分类
     */
    private Integer classify;

    /**
     * 事件类型
     */
    private Integer type;

    /**
     * 日志信息
     */
    private String log;

    /**
     * 日志参数
     */
    private String params;

    /**
     * 是否执行成功 1成功 2失败
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer result;

    /**
     * 开始时间区间-开始
     */
    private Date rangeStart;

    /**
     * 开始时间区间-结束
     */
    private Date rangeEnd;

    /**
     * 只看自己
     *
     * @see com.orion.ops.consts.Const#ENABLE
     */
    private Integer onlyMyself;

}
