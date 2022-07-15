package com.orion.ops.handler.app.action;

import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.io.Streams;
import com.orion.net.remote.channel.sftp.SftpExecutor;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.StainCode;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.utils.Utils;
import com.orion.spring.SpringHolder;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 执行操作-传输产物 sftp方式
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#RELEASE_TRANSFER
 * @see com.orion.ops.consts.app.TransferMode#SFTP
 * @since 2022/4/26 23:57
 */
public class SftpTransferActionHandler extends AbstractActionHandler {

    private static final String SPACE = "      ";

    protected static MachineEnvService machineEnvService = SpringHolder.getBean(MachineEnvService.class);

    private SftpExecutor executor;

    public SftpTransferActionHandler(Long actionId, MachineActionStore store) {
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
        // 拼接删除日志
        String transferPath = store.getTransferPath();
        StringBuilder removeLog = new StringBuilder(Const.LF)
                .append(SPACE)
                .append(Utils.getStainKeyWords("开始删除文件原始文件", StainCode.GLOSS_RED))
                .append(Const.LF);
        removeLog.append(SPACE)
                .append(Utils.getStainKeyWords("remove:    ", StainCode.GLOSS_RED))
                .append(Utils.getStainKeyWords(transferPath, StainCode.GLOSS_BLUE))
                .append(Const.LF);
        this.appendLog(removeLog.toString());
        // 删除远程文件
        executor.rm(transferPath);
        String bundleAbsolutePath = bundleFile.getAbsolutePath();
        // 拼接头文件
        StringBuilder headerLog = new StringBuilder(Const.LF)
                .append(SPACE)
                .append(Utils.getStainKeyWords("开始传输文件", StainCode.GLOSS_BLUE))
                .append(Const.LF)
                .append(SPACE)
                .append(Utils.getStainKeyWords("source:    ", StainCode.GLOSS_GREEN))
                .append(Utils.getStainKeyWords(bundleAbsolutePath, StainCode.GLOSS_BLUE))
                .append(Const.LF)
                .append(SPACE)
                .append(Utils.getStainKeyWords("target:    ", StainCode.GLOSS_GREEN))
                .append(Utils.getStainKeyWords(transferPath, StainCode.GLOSS_BLUE))
                .append(Const.LF_2);
        headerLog.append(StainCode.prefix(StainCode.GLOSS_GREEN))
                .append(SPACE)
                .append("类型")
                .append(SPACE)
                .append(" target")
                .append(StainCode.SUFFIX)
                .append(Const.LF);
        this.appendLog(headerLog.toString());
        // 转化文件
        Map<File, String> transferFiles = this.convertFile(bundleFile, transferPath);
        for (Map.Entry<File, String> entity : transferFiles.entrySet()) {
            File localFile = entity.getKey();
            String remoteFile = entity.getValue();
            // 文件夹则创建
            if (localFile.isDirectory()) {
                StringBuilder createDirLog = new StringBuilder(SPACE)
                        .append(Utils.getStainKeyWords("mkdir", StainCode.GLOSS_GREEN))
                        .append(SPACE)
                        .append(Utils.getStainKeyWords(remoteFile, StainCode.GLOSS_BLUE))
                        .append(Const.LF);
                this.appendLog(createDirLog.toString());
                executor.mkdirs(remoteFile);
                continue;
            }
            // 文件则传输
            StringBuilder transferLog = new StringBuilder(SPACE)
                    .append(Utils.getStainKeyWords("touch", StainCode.GLOSS_GREEN))
                    .append(SPACE)
                    .append(Utils.getStainKeyWords(remoteFile, StainCode.GLOSS_BLUE))
                    .append(StainCode.prefix(StainCode.GLOSS_BLUE))
                    .append(" (")
                    .append(Files1.getSize(localFile.length()))
                    .append(")")
                    .append(StainCode.SUFFIX)
                    .append(Const.LF);
            this.appendLog(transferLog.toString());
            executor.uploadFile(remoteFile, Files1.openInputStreamFast(localFile), true);
        }
        this.appendLog(Const.LF);
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
