package com.orion.ops.entity.vo.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * sftp 文件列表响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 18:30
 */
@Data
@ApiModel(value = "文件列表响应")
public class FileListVO {

    @ApiModelProperty(value = "当前路径")
    private String path;

    @ApiModelProperty(value = "文件")
    private List<FileDetailVO> files;

}
