package com.orion.ops.entity.vo.upload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 批量上传获取token响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/16 10:44
 */
@Data
@ApiModel(value = "批量上传获取token响应")
public class BatchUploadTokenVO {

    @ApiModelProperty(value = "accessToken")
    private String accessToken;

    @ApiModelProperty(value = "notifyToken")
    private String notifyToken;

}
