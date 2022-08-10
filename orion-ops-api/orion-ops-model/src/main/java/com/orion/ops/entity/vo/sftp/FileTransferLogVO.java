package com.orion.ops.entity.vo.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 传输列表响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/27 23:30
 */
@Data
@ApiModel(value = "传输列表响应")
@SuppressWarnings("ALL")
public class FileTransferLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "fileToken")
    private String fileToken;

    /**
     * @see com.orion.ops.constant.sftp.SftpTransferType
     */
    @ApiModelProperty(value = "传输类型 10上传 20下载 30传输")
    private Integer type;

    @ApiModelProperty(value = "远程文件")
    private String remoteFile;

    @ApiModelProperty(value = "当前大小")
    private String current;

    @ApiModelProperty(value = "文件大小")
    private String size;

    @ApiModelProperty(value = "当前进度")
    private Double progress;

    /**
     * @see com.orion.ops.constant.sftp.SftpTransferStatus
     */
    @ApiModelProperty(value = "传输状态 10未开始 20进行中 30已暂停 40已完成 50已取消 60传输异常")
    private Integer status;

}
