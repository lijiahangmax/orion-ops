package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * sftp 删除文件命令
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 23:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "删除文件命令")
public class FileRemoveRequest extends FileBaseRequest {

    @ApiModelProperty(value = "绝对路径")
    private List<String> paths;

}
