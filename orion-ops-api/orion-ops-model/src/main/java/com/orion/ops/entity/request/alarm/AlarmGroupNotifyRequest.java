package com.orion.ops.entity.request.alarm;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报警组通知方式请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/25 15:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "报警组通知方式请求")
@SuppressWarnings("ALL")
public class AlarmGroupNotifyRequest extends PageRequest {

    @ApiModelProperty(value = "通知id")
    private Long notifyId;

    /**
     * @see com.orion.ops.constant.alarm.AlarmGroupNotifyType
     */
    @ApiModelProperty(value = "通知类型")
    private Integer notifyType;

}