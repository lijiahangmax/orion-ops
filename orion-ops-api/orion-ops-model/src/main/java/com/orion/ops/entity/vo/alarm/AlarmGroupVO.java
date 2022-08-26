package com.orion.ops.entity.vo.alarm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 报警组响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/25 17:35
 */
@Data
@ApiModel(value = "报警组响应")
public class AlarmGroupVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "组名称")
    private String name;

    @ApiModelProperty(value = "组描述")
    private String description;

    @ApiModelProperty(value = "组员名称")
    private String groupUsers;

    @ApiModelProperty(value = "报警组员id")
    private List<Long> userIdList;

    @ApiModelProperty(value = "报警通知方式")
    private List<Long> notifyIdList;

}
