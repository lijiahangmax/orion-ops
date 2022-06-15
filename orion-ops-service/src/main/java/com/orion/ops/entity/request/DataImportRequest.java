package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据导入请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 17:33
 */
@Data
@ApiModel(value = "数据导入请求")
public class DataImportRequest {

    @ApiModelProperty(value = "导入token")
    private String importToken;

}
