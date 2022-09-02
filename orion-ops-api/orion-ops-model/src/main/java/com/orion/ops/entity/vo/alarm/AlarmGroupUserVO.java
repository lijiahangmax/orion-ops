package com.orion.ops.entity.vo.alarm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 报警组员响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 16:29
 */
@Data
@ApiModel(value = "报警组员响应")
public class AlarmGroupUserVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户花名")
    private String nickname;

}
