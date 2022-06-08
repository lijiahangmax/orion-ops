package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "操作日志请求")
public class EventLogRequest extends PageRequest {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * @see com.orion.ops.consts.event.EventClassify
     */
    @ApiModelProperty(value = "事件分类")
    private Integer classify;

    /**
     * @see com.orion.ops.consts.event.EventType
     */
    @ApiModelProperty(value = "事件类型")
    private Integer type;

    @ApiModelProperty(value = "日志信息")
    private String log;

    @ApiModelProperty(value = "日志参数")
    private String params;

    /**
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    @ApiModelProperty(value = "是否执行成功 1成功 2失败")
    private Integer result;

    @ApiModelProperty(value = "开始时间区间-开始")
    private Date rangeStart;

    @ApiModelProperty(value = "开始时间区间-结束")
    private Date rangeEnd;

    /**
     * @see com.orion.ops.consts.Const#ENABLE
     */
    @ApiModelProperty(value = "只看自己")
    private Integer onlyMyself;

}
