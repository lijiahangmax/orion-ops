package com.orion.ops.entity.request.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 终端配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 22:14
 */
@Data
@ApiModel(value = "终端配置请求")
@SuppressWarnings("ALL")
public class MachineTerminalRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    /**
     * @see com.orion.net.remote.TerminalType
     */
    @ApiModelProperty(value = "终端类型")
    private String terminalType;

    @ApiModelProperty(value = "背景色")
    private String backgroundColor;

    @ApiModelProperty(value = "字体颜色")
    private String fontColor;

    @ApiModelProperty(value = "字体大小")
    private Integer fontSize;

    @ApiModelProperty(value = "字体名称")
    private String fontFamily;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "是否开启url link 1开启 2关闭")
    private Integer enableWebLink;

}
