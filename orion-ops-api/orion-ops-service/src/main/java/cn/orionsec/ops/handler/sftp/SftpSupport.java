/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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

import cn.orionsec.kit.lang.id.UUIds;
import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.kit.net.host.sftp.SftpExecutor;
import cn.orionsec.ops.constant.sftp.SftpTransferStatus;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.handler.sftp.impl.UploadFileProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * sftp工具
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/13 11:05
 */
@Slf4j
public class SftpSupport {

    private SftpSupport() {
    }

    /**
     * 检查远程机器和本机是否是同一台机器
     *
     * @param executor executor
     * @return 是否为本机
     */
    public static boolean checkUseFileSystem(SftpExecutor executor) {
        try {
            // 创建一个临时文件
            String checkPath = Files1.getPath(SystemEnvAttr.TEMP_PATH.getValue(), UUIds.random32() + ".ck");
            File checkFile = new File(checkPath);
            Files1.touch(checkFile);
            checkFile.deleteOnExit();
            // 查询远程机器是否有此文件 如果有则证明传输机器和宿主机是同一台
            boolean exist = executor.getFile(checkFile.getAbsolutePath()) != null;
            Files1.delete(checkFile);
            return exist;
        } catch (Exception e) {
            log.error("无法使用FSC {}", Exceptions.getDigest(e));
            return false;
        }
    }

    /**
     * 使用 file system copy
     *
     * @param processor processor
     */
    public static void usingFsCopy(FileTransferProcessor processor) {
        String remoteFile = processor.record.getRemoteFile();
        String localFile = processor.record.getLocalFile();
        String localAbsolutePath = Files1.getPath(SystemEnvAttr.SWAP_PATH.getValue(), localFile);
        log.info("sftp文件传输-使用FSC fileToken: {}, machineId: {}, local: {}, remote: {}",
                processor.fileToken, processor.machineId, localAbsolutePath, remoteFile);
        // 复制
        File sourceFile;
        File targetFile;
        if (processor instanceof UploadFileProcessor) {
            sourceFile = new File(localAbsolutePath);
            targetFile = new File(remoteFile);
        } else {
            sourceFile = new File(remoteFile);
            targetFile = new File(localAbsolutePath);
        }
        Files1.copy(sourceFile, targetFile);
        // 通知进度
        long fileSize = sourceFile.length();
        processor.notifyProgress(Files1.getSize(fileSize), Files1.getSize(fileSize), "100");
        // 通知状态
        processor.updateStatusAndNotify(SftpTransferStatus.FINISH.getStatus(), 100D, fileSize);
    }

}
