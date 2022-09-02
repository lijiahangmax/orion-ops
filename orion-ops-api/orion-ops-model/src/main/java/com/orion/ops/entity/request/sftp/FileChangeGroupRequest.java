package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp 修改文件组请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 20:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "修改文件组请求")
public class FileChangeGroupRequest extends FileBaseRequest {

    @ApiModelProperty(value = "绝对路径")
    private String path;

    @ApiModelProperty(value = "组id")
    private Integer gid;

}
