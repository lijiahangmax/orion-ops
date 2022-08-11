package com.orion.ops.handler.sftp.hint;

import com.orion.ops.entity.domain.FileTransferLogDO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 文件打包参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/17 17:53
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FilePackageHint extends FileTransferHint {

    /**
     * 压缩文件列表
     */
    private List<FileTransferLogDO> fileList;

    public FilePackageHint(FileTransferLogDO record, List<FileTransferLogDO> fileList) {
        super(record);
        this.fileList = fileList;
    }

}
