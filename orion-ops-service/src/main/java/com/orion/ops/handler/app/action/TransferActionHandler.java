package com.orion.ops.handler.app.action;

import com.orion.ops.consts.system.SystemEnvAttr;
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
 * 执行操作-传输产物
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#RELEASE_TRANSFER
 * @since 2022/2/11 16:01
 */
public class TransferActionHandler extends AbstractActionHandler {

    protected static MachineEnvService machineEnvService = SpringHolder.getBean(MachineEnvService.class);

    private SftpExecutor executor;

    public TransferActionHandler(Long actionId, MachineActionStore store) {
        super(actionId, store);
    }

    @Override
    protected void handler() throws Exception {
        // 检查文件
        String bundlePath = Files1.getPath(SystemEnvAttr.DIST_PATH.getValue(), store.getBundlePath());
        File bundleFile = new File(bundlePath);
        if (!bundleFile.exists()) {
            throw Exceptions.log("*** 产物文件不存在 " + bundlePath);
        }
        // 打开executor
        String charset = machineEnvService.getSftpCharset(store.getMachineId());
        this.executor = store.getSessionStore().getSftpExecutor(charset);
        executor.connect();
        // 删除远程文件
        String transferPath = store.getTransferPath();
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
    public void terminate() {
        super.terminate();
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
