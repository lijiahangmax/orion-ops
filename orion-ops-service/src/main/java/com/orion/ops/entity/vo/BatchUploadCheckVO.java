package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 批量上传文件检查响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 16:29
 */
@Data
@ApiModel(value = "批量上传文件检查响应")
public class BatchUploadCheckVO {

    @ApiModelProperty(value = "可连接的机器id")
    private Set<Long> connectedMachineIdList;

    @ApiModelProperty(value = "可以连接的机器")
    private List<BatchUploadCheckMachineVO> connectedMachines;

    @ApiModelProperty(value = "无法连接的机器")
    private List<BatchUploadCheckMachineVO> notConnectedMachines;

    @ApiModelProperty(value = "机器已存在的文件")
    private List<BatchUploadCheckFileVO> machinePresentFiles;

}
