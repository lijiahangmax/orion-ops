package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.io.Files1;
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
public class FileTransferLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "fileToken")
    private String fileToken;

    /**
     * @see com.orion.ops.consts.sftp.SftpTransferType
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
     * @see com.orion.ops.consts.sftp.SftpTransferStatus
     */
    @ApiModelProperty(value = "传输状态 10未开始 20进行中 30已暂停 40已完成 50已取消 60传输异常")
    private Integer status;

    static {
        TypeStore.STORE.register(FileTransferLogDO.class, FileTransferLogVO.class, p -> {
            FileTransferLogVO vo = new FileTransferLogVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setFileToken(p.getFileToken());
            vo.setType(p.getTransferType());
            vo.setRemoteFile(p.getRemoteFile());
            vo.setCurrent(Files1.getSize(p.getCurrentSize()));
            vo.setSize(Files1.getSize(p.getFileSize()));
            vo.setProgress(p.getNowProgress());
            vo.setStatus(p.getTransferStatus());
            return vo;
        });
    }

}
