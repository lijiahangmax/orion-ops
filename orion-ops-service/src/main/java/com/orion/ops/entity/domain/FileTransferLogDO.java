package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * sftp传输日志表
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_transfer_log")
public class FileTransferLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * token
     */
    @TableField("token")
    private String token;

    /**
     * 操作类型 10上传 20下载 30传输
     *
     * @see com.orion.ops.consts.sftp.SftpOperatorType
     */
    @TableField("operator_type")
    private Integer operatorType;

    /**
     * 机器id
     */
    @TableField("machine_id")
    private Long machineId;

    /**
     * 远程文件
     */
    @TableField("remote_file")
    private String remoteFile;

    /**
     * 本机文件
     */
    @TableField("local_file")
    private String localFile;

    /**
     * 文件大小
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 当前进度
     */
    @TableField("now_progress")
    private Double nowProgress;

    /**
     * 传输状态 10未开始 20进行中 30已暂停 40已取消 50已完成 60传输异常
     *
     * @see com.orion.ops.consts.sftp.SftpTransferStatus
     */
    @TableField("transfer_status")
    private Integer transferStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
