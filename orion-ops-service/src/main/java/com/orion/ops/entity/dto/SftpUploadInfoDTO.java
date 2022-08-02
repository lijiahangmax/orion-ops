package com.orion.ops.entity.dto;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.request.BatchUploadRequest;
import com.orion.ops.entity.request.sftp.FileUploadRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * sftp 文件上传对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 17:04
 */
@Data
@ApiModel(value = "sftp 文件上传对象")
public class SftpUploadInfoDTO {

    @ApiModelProperty(value = "远程路径")
    private String remotePath;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器id")
    private List<Long> machineIdList;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    static {
        TypeStore.STORE.register(BatchUploadRequest.class, SftpUploadInfoDTO.class, p -> {
            SftpUploadInfoDTO dto = new SftpUploadInfoDTO();
            dto.setRemotePath(p.getRemotePath());
            dto.setMachineIdList(p.getMachineIds());
            return dto;
        });
        TypeStore.STORE.register(FileUploadRequest.class, SftpUploadInfoDTO.class, p -> {
            SftpUploadInfoDTO dto = new SftpUploadInfoDTO();
            dto.setRemotePath(p.getRemotePath());
            return dto;
        });
    }

}
