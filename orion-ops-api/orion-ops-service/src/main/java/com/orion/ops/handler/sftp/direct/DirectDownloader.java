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
package com.orion.ops.handler.sftp.direct;

import com.orion.lang.able.SafeCloseable;
import com.orion.lang.utils.Valid;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.io.Streams;
import com.orion.net.remote.channel.SessionStore;
import com.orion.net.remote.channel.sftp.SftpExecutor;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.handler.sftp.SftpSupport;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.spring.SpringHolder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件直接上传
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/13 10:47
 */
@Slf4j
public class DirectDownloader implements SafeCloseable {

    private static final MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private static final MachineEnvService machineEnvService = SpringHolder.getBean(MachineEnvService.class);

    /**
     * 机器id
     */
    private final Long machineId;

    /**
     * session
     */
    private SessionStore session;

    /**
     * 执行器
     */
    private SftpExecutor executor;

    public DirectDownloader(Long machineId) {
        this.machineId = machineId;
    }

    /**
     * 打开连接
     *
     * @return this
     */
    public DirectDownloader open() {
        log.info("直接下载远程文件-建立连接-开始 machineId: {}", machineId);
        try {
            this.session = machineInfoService.openSessionStore(machineId);
            log.info("直接下载远程文件-建立连接-成功 machineId: {}", machineId);
            return this;
        } catch (Exception e) {
            log.error("直接下载远程文件-建立连接-失败 machineId: {}, e: {}", machineId, e);
            throw e;
        }
    }

    /**
     * 获取文件
     *
     * @param path path
     * @return 文件流
     * @throws IOException IOException
     */
    public InputStream getFile(String path) throws IOException {
        log.info("直接下载远程文件-开始执行 machineId: {}, path: {}", machineId, path);
        Valid.notNull(session, MessageConst.UNCONNECTED);
        try {
            // 打开执行器
            String charset = machineEnvService.getSftpCharset(machineId);
            this.executor = session.getSftpExecutor(charset);
            executor.connect();
            // 检查是否为本机
            if (SftpSupport.checkUseFileSystem(executor)) {
                // 是本机则返回文件流
                return Files1.openInputStreamFast(path);
            } else {
                // 不是本机获取sftp文件
                return executor.openInputStream(path);
            }
        } catch (IOException e) {
            log.error("直接下载远程文件-执行失败 machineId: {}, path: {}, e: {}", machineId, path, e);
            throw e;
        }
    }

    /**
     * 关闭执行器
     */
    public void closeExecutor() {
        Streams.close(executor);
    }

    @Override
    public void close() {
        Streams.close(executor);
        Streams.close(session);
    }

}
