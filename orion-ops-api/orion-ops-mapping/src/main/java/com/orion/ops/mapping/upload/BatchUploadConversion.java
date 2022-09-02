package com.orion.ops.mapping.upload;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.dto.sftp.SftpUploadInfoDTO;
import com.orion.ops.entity.request.upload.BatchUploadRequest;
import com.orion.ops.entity.vo.upload.BatchUploadCheckFileVO;
import com.orion.ops.entity.vo.upload.BatchUploadCheckMachineVO;

/**
 * 文件批量上传 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 17:04
 */
public class BatchUploadConversion {

    static {
        TypeStore.STORE.register(BatchUploadRequest.class, SftpUploadInfoDTO.class, p -> {
            SftpUploadInfoDTO dto = new SftpUploadInfoDTO();
            dto.setRemotePath(p.getRemotePath());
            dto.setMachineIdList(p.getMachineIds());
            return dto;
        });
    }

    static {
        TypeStore.STORE.register(MachineInfoDO.class, BatchUploadCheckFileVO.class, p -> {
            BatchUploadCheckFileVO vo = new BatchUploadCheckFileVO();
            vo.setId(p.getId());
            vo.setName(p.getMachineName());
            vo.setHost(p.getMachineHost());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(MachineInfoDO.class, BatchUploadCheckMachineVO.class, p -> {
            BatchUploadCheckMachineVO vo = new BatchUploadCheckMachineVO();
            vo.setId(p.getId());
            vo.setName(p.getMachineName());
            vo.setHost(p.getMachineHost());
            return vo;
        });
    }

}
