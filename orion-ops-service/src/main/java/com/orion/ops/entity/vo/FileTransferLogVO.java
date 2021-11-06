package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.io.Files1;
import lombok.Data;

/**
 * 传输列表
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/27 23:30
 */
@Data
public class FileTransferLogVO {

    /**
     * id
     */
    private Long id;

    /**
     * fileToken
     */
    private String fileToken;

    /**
     * 传输类型 10上传 20下载 30传输
     *
     * @see com.orion.ops.consts.sftp.SftpTransferType
     */
    private Integer type;

    /**
     * 远程文件
     */
    private String remoteFile;

    /**
     * 当前大小
     */
    private String current;

    /**
     * 文件大小
     */
    private String size;

    /**
     * 当前进度
     */
    private Double progress;

    /**
     * 传输状态 10未开始 20进行中 30已暂停 40已完成 50传输异常
     *
     * @see com.orion.ops.consts.sftp.SftpTransferStatus
     */
    private Integer status;

    static {
        TypeStore.STORE.register(FileTransferLogDO.class, FileTransferLogVO.class, s -> {
            FileTransferLogVO vo = new FileTransferLogVO();
            vo.setId(s.getId());
            vo.setFileToken(s.getFileToken());
            vo.setType(s.getTransferType());
            vo.setRemoteFile(s.getRemoteFile());
            vo.setCurrent(Files1.getSize(s.getCurrentSize()));
            vo.setSize(Files1.getSize(s.getFileSize()));
            vo.setProgress(s.getNowProgress());
            vo.setStatus(s.getTransferStatus());
            return vo;
        });
    }

}
