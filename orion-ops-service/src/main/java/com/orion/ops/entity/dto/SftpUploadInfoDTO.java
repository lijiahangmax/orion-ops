package com.orion.ops.entity.dto;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.request.BatchUploadRequest;
import com.orion.ops.entity.request.sftp.FileUploadRequest;
import lombok.Data;

import java.util.List;

/**
 * sftp 上传对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 17:04
 */
@Data
public class SftpUploadInfoDTO {

    /**
     * 远程路径
     */
    private String remotePath;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器id
     */
    private List<Long> machineIdList;

    /**
     * 用户id
     */
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
