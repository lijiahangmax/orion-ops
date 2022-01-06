package com.orion.ops.handler.release.handler;

import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.handler.release.ReleaseStore;
import com.orion.ops.handler.release.machine.MachineStore;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.collect.Maps;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 发布传输处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/28 18:04
 */
public class TransferReleaseHandler extends AbstractReleaseHandler {

    protected static MachineEnvService machineEnvService = SpringHolder.getBean(MachineEnvService.class);

    private SftpExecutor executor;

    public TransferReleaseHandler(Long actionId, ReleaseStore store, MachineStore machineStore) {
        super(actionId, store, machineStore);
    }

    @Override
    protected void handler() throws Exception {
        // 检查文件
        ApplicationReleaseDO release = store.getRecord();
        String bundlePath = Files1.getPath(MachineEnvAttr.DIST_PATH.getValue(), release.getBundlePath());
        File bundleFile = new File(bundlePath);
        if (!bundleFile.exists()) {
            throw Exceptions.log("*** 产物文件不存在 " + bundlePath);
        }
        // 打开executor
        String charset = machineEnvService.getSftpCharset(machineStore.getMachineId());
        this.executor = machineStore.getSessionStore().getSftpExecutor(charset);
        executor.connect();
        // 删除远程文件
        String transferPath = release.getTransferPath();
        executor.rm(transferPath);
        // 转化文件
        Map<File, String> transferFiles = this.convertFile(bundleFile, transferPath);
        for (Map.Entry<File, String> entity : transferFiles.entrySet()) {
            File localFile = entity.getKey();
            String remoteFile = entity.getValue();
            if (localFile.isDirectory()) {
                super.appendLog("*** 开始传输创建文件夹 " + remoteFile);
                executor.mkdirs(remoteFile);
                return;
            }
            super.appendLog("*** 开始传输文件 " + localFile.getAbsolutePath() + " >>> " + remoteFile + "  size: " + Files1.getSize(localFile.length()) + "\n");
            executor.uploadFile(remoteFile, Files1.openInputStreamFast(localFile), true);
        }
    }

    /**
     * 转化文件
     *
     * @param bundleFile   打包文件
     * @param transferPath 传输目录
     * @return transferFiles
     */
    private Map<File, String> convertFile(File bundleFile, String transferPath) {
        Map<File, String> map = Maps.newLinkedMap();
        if (bundleFile.isFile()) {
            map.put(bundleFile, transferPath);
            return map;
        }
        // 如果是文件夹则需要截取
        String bundleFileAbsolutePath = bundleFile.getAbsolutePath();
        List<File> transferFiles = Files1.listFiles(bundleFile, true, true);
        for (File transferFile : transferFiles) {
            String remoteFile = Files1.getPath(transferPath, transferFile.getAbsolutePath().substring(bundleFileAbsolutePath.length() + 1));
            map.put(transferFile, remoteFile);
        }
        return map;
    }

    @Override
    public Integer getExitCode() {
        return null;
    }

    @Override
    public void terminated() {
        super.terminated();
        // 关闭executor
        Streams.close(executor);
    }

    @Override
    public void close() {
        super.close();
        // 关闭executor
        Streams.close(executor);
    }

}
