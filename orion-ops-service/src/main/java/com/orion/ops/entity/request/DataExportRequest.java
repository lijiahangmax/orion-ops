package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据导出请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 16:10
 */
@Data
@ApiModel(value = "数据导出请求")
public class DataExportRequest {

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "是否导出密码")
    private Integer exportPassword;

    @ApiModelProperty(value = "保护密码")
    private String protectPassword;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "分类")
    private Integer classify;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "只看自己")
    private Integer onlyMyself;

}
