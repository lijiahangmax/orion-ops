package com.orion.ops.entity.request.alarm;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 报警组请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/25 15:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "报警组请求")
public class AlarmGroupRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "报警组名称")
    private String name;

    @ApiModelProperty(value = "报警组描述")
    private String description;

    @ApiModelProperty(value = "报警组员id")
    private List<Long> userIdList;

    @ApiModelProperty(value = "报警通知方式")
    private List<Long> notifyIdList;

}
