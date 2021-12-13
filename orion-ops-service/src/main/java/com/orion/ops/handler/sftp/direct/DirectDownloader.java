package com.orion.ops.handler.sftp.direct;

import com.orion.able.SafeCloseable;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.handler.sftp.SftpSupport;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Valid;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
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

    private static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private static MachineEnvService machineEnvService = SpringHolder.getBean(MachineEnvService.class);

    /**
     * 机器id
     */
    private Long machineId;

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
                return executor.getInputStream(path);
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
