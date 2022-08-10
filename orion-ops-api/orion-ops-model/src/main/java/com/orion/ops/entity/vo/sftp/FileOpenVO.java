package com.orion.ops.entity.vo.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp 打开连接响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 18:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "打开连接响应")
public class FileOpenVO extends FileListVO {

    @ApiModelProperty(value = "根目录")
    private String home;

    @ApiModelProperty(value = "sessionToken")
    private String sessionToken;

    @ApiModelProperty(value = "编码格式")
    private String charset;

}
