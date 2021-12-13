package com.orion.ops.handler.sftp;

import com.orion.id.UUIds;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.utils.io.Files1;

import java.io.File;

/**
 * sftp工具
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/13 11:05
 */
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
        // 创建一个临时文件
        String checkPath = Files1.getPath(MachineEnvAttr.TEMP_PATH.getValue(), UUIds.random32() + ".ck");
        File checkFile = new File(checkPath);
        Files1.touch(checkFile);
        checkFile.deleteOnExit();
        // 查询远程机器是否有此文件 如果有则证明传输机器和宿主机是同一台
        boolean exist = executor.getFile(checkFile.getAbsolutePath()) != null;
        Files1.delete(checkFile);
        return exist;
    }

}
