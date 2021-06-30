package com.orion.ops.handler.sftp;

import com.orion.able.Executable;
import com.orion.able.Renewable;
import com.orion.able.Stoppable;
import com.orion.function.select.Branches;
import com.orion.function.select.Selector;
import com.orion.ops.consts.sftp.SftpTransferType;
import com.orion.ops.handler.sftp.impl.DownloadFileProcessor;
import com.orion.ops.handler.sftp.impl.UploadFileProcessor;
import com.orion.remote.channel.SessionStore;

/**
 * sftp 传输接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/29 19:08
 */
public interface IFileTransferProcessor extends Runnable, Stoppable, Executable, Renewable {

    /**
     * 获取实际执行processor
     *
     * @param hint    hint
     * @param session session
     * @return IFileTransferProcessor
     */
    static IFileTransferProcessor of(FileTransferHint hint, SessionStore session) {
        return Selector.<SftpTransferType, IFileTransferProcessor>of(hint.getTransferType())
                .test(Branches.eq(SftpTransferType.UPLOAD).then(new UploadFileProcessor(hint, session)))
                .test(Branches.eq(SftpTransferType.DOWNLOAD).then(new DownloadFileProcessor(hint, session)))
                .get();
    }

}
