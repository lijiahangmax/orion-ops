package com.orion.ops.handler.sftp.hint;

import com.orion.ops.consts.sftp.SftpTransferType;
import com.orion.ops.entity.domain.FileTransferLogDO;
import lombok.Data;

import java.util.List;

/**
 * 文件传输配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/17 17:48
 */
@Data
public class FileTransferHint {

    /**
     * record
     */
    private FileTransferLogDO record;

    /**
     * 类型
     */
    private SftpTransferType type;

    public FileTransferHint(FileTransferLogDO record) {
        this.record = record;
        this.type = SftpTransferType.of(record.getTransferType());
    }

    /**
     * 获取上传配置
     *
     * @param record record
     * @return 上传配置
     */
    public static FileTransferHint transfer(FileTransferLogDO record) {
        return new FileTransferHint(record);
    }

    /**
     * 获取打包配置
     *
     * @param record   record
     * @param fileList fileList
     * @return 上传配置
     */
    public static FilePackageHint packaged(FileTransferLogDO record, List<FileTransferLogDO> fileList) {
        return new FilePackageHint(record, fileList);
    }

}
