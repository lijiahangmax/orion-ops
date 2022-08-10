package com.orion.ops.entity.vo.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * sftp 文件详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 18:36
 */
@Data
@ApiModel(value = "文件详情响应")
public class FileDetailVO {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "绝对路径")
    private String path;

    @ApiModelProperty(value = "文件大小")
    private String size;

    @ApiModelProperty(value = "文件大小(byte)")
    private Long sizeByte;

    @ApiModelProperty(value = "属性")
    private String attr;

    @ApiModelProperty(value = "10进制表现的8进制权限")
    private Integer permission;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "组id")
    private Integer gid;

    @ApiModelProperty(value = "更新时间")
    private Date modifyTime;

    @ApiModelProperty(value = "是否为目录")
    private Boolean isDir;

    @ApiModelProperty(value = "是否安全")
    private Boolean isSafe;

}
