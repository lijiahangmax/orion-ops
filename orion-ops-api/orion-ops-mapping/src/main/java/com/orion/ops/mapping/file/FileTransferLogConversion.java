package com.orion.ops.mapping.file;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.vo.sftp.FileTransferLogVO;

/**
 * 文件传输日志 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:07
 */
public class FileTransferLogConversion {

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
