package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * sftp 检查文件是否存在
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/10/25 9:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "检查文件是否存在请求")
public class FilePresentCheckRequest extends FileBaseRequest {

    @ApiModelProperty(value = "当前路径")
    private String path;

    @ApiModelProperty(value = "文件名称")
    private List<String> names;

    @ApiModelProperty(value = "文件大小")
    private Long size;

}
