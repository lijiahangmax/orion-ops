package com.orion.ops.entity.vo.tail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 文件tail响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/20 19:17
 */
@Data
@ApiModel(value = "文件tail响应")
@SuppressWarnings("ALL")
public class FileTailVO {

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "机器状态 1有效 2无效")
    private Integer machineStatus;

    @ApiModelProperty(value = "文件路径")
    private String path;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * @see com.orion.ops.constant.Const#TAIL_OFFSET_LINE
     */
    @ApiModelProperty(value = "偏移量")
    private Integer offset;

    /**
     * @see com.orion.ops.constant.Const#UTF_8
     */
    @ApiModelProperty(value = "编码集")
    private String charset;

    @ApiModelProperty(value = "命令")
    private String command;

    /**
     * @see com.orion.ops.constant.tail.FileTailMode
     */
    @ApiModelProperty(value = "宿主机文件追踪类型 tracker/tail")
    private String tailMode;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTimeAgo;

}
