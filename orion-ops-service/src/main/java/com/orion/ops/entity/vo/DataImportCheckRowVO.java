package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据导入检查响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/27 11:18
 */
@Data
@ApiModel(value = "数据导入检查响应")
public class DataImportCheckRowVO {

    @ApiModelProperty(value = "索引 0开始")
    private Integer index;

    @ApiModelProperty(value = "行号 前端提示")
    private Integer row;

    @ApiModelProperty(value = "唯一标识")
    private String symbol;

    @ApiModelProperty(value = "非法信息")
    private String illegalMessage;

    @ApiModelProperty(value = "数据id 更新操作")
    private Long id;

}
