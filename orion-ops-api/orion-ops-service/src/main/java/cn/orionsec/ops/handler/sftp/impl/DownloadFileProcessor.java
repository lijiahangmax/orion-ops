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
package cn.orionsec.ops.handler.sftp.impl;

import cn.orionsec.kit.lang.utils.Threads;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.kit.net.host.sftp.transfer.SftpDownloader;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.entity.domain.FileTransferLogDO;
import cn.orionsec.ops.handler.sftp.FileTransferProcessor;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件下载处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/25 18:38
 */
@Slf4j
public class DownloadFileProcessor extends FileTransferProcessor {

    public DownloadFileProcessor(FileTransferLogDO record) {
        super(record);
    }

    @Override
    public void exec() {
        String localFile = record.getLocalFile();
        String localAbsolutePath = Files1.getPath(SystemEnvAttr.SWAP_PATH.getValue(), localFile);
        log.info("sftp文件下载-提交任务 fileToken: {}, machineId: {}, local: {}, remote: {}, record: {}",
                fileToken, machineId, localAbsolutePath, record.getRemoteFile(), JSON.toJSONString(record));
        Threads.start(this, SchedulerPools.SFTP_DOWNLOAD_SCHEDULER);
    }

    @Override
    protected void handler() {
        String remoteFile = record.getRemoteFile();
        String localFile = record.getLocalFile();
        String localAbsolutePath = Files1.getPath(SystemEnvAttr.SWAP_PATH.getValue(), localFile);
        log.info("sftp文件下载-开始传输 fileToken: {}, machineId: {}, local: {}, remote: {}",
                fileToken, machineId, localAbsolutePath, remoteFile);
        SftpDownloader download = executor.download(remoteFile, localAbsolutePath);
        this.initProgress(download.getProgress());
        download.run();
    }

}
