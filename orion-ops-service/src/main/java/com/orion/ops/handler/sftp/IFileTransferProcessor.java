package com.orion.ops.handler.sftp;

import com.orion.able.Executable;
import com.orion.able.Stoppable;
import com.orion.function.select.Branches;
import com.orion.function.select.Selector;
import com.orion.ops.consts.sftp.SftpTransferType;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.handler.sftp.hint.FilePackageHint;
import com.orion.ops.handler.sftp.hint.FileTransferHint;
import com.orion.ops.handler.sftp.impl.DownloadFileProcessor;
import com.orion.ops.handler.sftp.impl.PackageFileProcessor;
import com.orion.ops.handler.sftp.impl.UploadFileProcessor;

/**
 * sftp 传输接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/29 19:08
 */
public interface IFileTransferProcessor extends Runnable, Stoppable, Executable {

    /**
     * 获取执行processor
     *
     * @param hint hint
     * @return IFileTransferProcessor
     */
    static IFileTransferProcessor of(FileTransferHint hint) {
        FileTransferLogDO record = hint.getRecord();
        return Selector.<SftpTransferType, IFileTransferProcessor>of(hint.getType())
                .test(Branches.eq(SftpTransferType.UPLOAD)
                        .then(() -> new UploadFileProcessor(record)))
                .test(Branches.eq(SftpTransferType.DOWNLOAD)
                        .then(() -> new DownloadFileProcessor(record)))
                .test(Branches.eq(SftpTransferType.PACKAGE)
                        .then(() -> new PackageFileProcessor(record, ((FilePackageHint) hint).getFileList())))
                .get();
    }

}
