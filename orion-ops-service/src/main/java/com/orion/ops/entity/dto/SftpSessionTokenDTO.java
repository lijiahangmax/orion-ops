package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * sftp 会话信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/16 9:41
 */
@Data
@ApiModel(value = "sftp 会话信息")
public class SftpSessionTokenDTO {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器id (批量上传用)")
    private List<Long> machineIdList;

}
