package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * sftp传输日志表
 *
 * @author Jiahang Li
 * @since 2021-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sftp传输日志表")
@TableName("file_transfer_log")
public class FileTransferLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "文件token")
    @TableField("file_token")
    private String fileToken;

    /**
     * @see com.orion.ops.constant.sftp.SftpTransferType
     */
    @ApiModelProperty(value = "传输类型 10上传 20下载 30传输")
    @TableField("transfer_type")
    private Integer transferType;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Long machineId;

    @ApiModelProperty(value = "远程文件")
    @TableField("remote_file")
    private String remoteFile;

    @ApiModelProperty(value = "本机文件")
    @TableField("local_file")
    private String localFile;

    @ApiModelProperty(value = "当前大小")
    @TableField("current_size")
    private Long currentSize;

    @ApiModelProperty(value = "文件大小")
    @TableField("file_size")
    private Long fileSize;

    @ApiModelProperty(value = "当前进度")
    @TableField("now_progress")
    private Double nowProgress;

    /**
     * @see com.orion.ops.constant.sftp.SftpTransferStatus
     */
    @ApiModelProperty(value = "传输状态 10未开始 20进行中 30已暂停 40已完成 50已取消 60传输异常")
    @TableField("transfer_status")
    private Integer transferStatus;

    /**
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
