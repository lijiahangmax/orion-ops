/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.handler.sftp;

import cn.orionsec.kit.lang.able.Executable;
import cn.orionsec.kit.lang.able.Stoppable;
import cn.orionsec.kit.lang.function.select.Branches;
import cn.orionsec.kit.lang.function.select.Selector;
import cn.orionsec.ops.constant.sftp.SftpTransferType;
import cn.orionsec.ops.entity.domain.FileTransferLogDO;
import cn.orionsec.ops.handler.sftp.hint.FilePackageHint;
import cn.orionsec.ops.handler.sftp.hint.FileTransferHint;
import cn.orionsec.ops.handler.sftp.impl.DownloadFileProcessor;
import cn.orionsec.ops.handler.sftp.impl.PackageFileProcessor;
import cn.orionsec.ops.handler.sftp.impl.UploadFileProcessor;

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
