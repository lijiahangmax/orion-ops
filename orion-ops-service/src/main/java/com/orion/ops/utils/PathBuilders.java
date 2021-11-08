package com.orion.ops.utils;

import com.orion.id.ObjectIds;
import com.orion.ops.consts.Const;
import com.orion.utils.Systems;
import com.orion.utils.io.Files1;
import com.orion.utils.time.Dates;

/**
 * 公共路径构建
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/23 12:43
 */
public class PathBuilders {

    private PathBuilders() {
    }

    /**
     * 获取 terminal 日志路径
     *
     * @param userId userId
     * @return path
     */
    public static String getTerminalLogPath(Long userId) {
        return Const.TERMINAL_LOG_DIR
                + "/" + Dates.current(Dates.YMD_HMS2)
                + "_" + userId
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取 exec 日志路径
     *
     * @param suffix    suffix
     * @param execId    execId
     * @param machineId machineId
     * @return path
     */
    public static String getExecLogPath(String suffix, Long execId, Long machineId) {
        return Const.EXEC_LOG_DIR + suffix
                + "/" + execId
                + "_" + machineId
                + "_" + Dates.current(Dates.YMD_HMS2)
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取 release 宿主机日志文件
     *
     * @param releaseId releaseId
     * @return path
     */
    public static String getReleaseHostLogPath(Long releaseId) {
        return Const.RELEASE_LOG_DIR
                + "/" + releaseId
                + Const.RELEASE_HOST_LOG_PREFIX
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取 release 目标机器日志文件
     *
     * @param releaseId releaseId
     * @param machineId 目标机器
     * @return path
     */
    public static String getReleaseTargetMachineLogPath(Long releaseId, Long machineId) {
        return Const.RELEASE_LOG_DIR
                + "/" + releaseId
                + Const.RELEASE_TARGET_LOG_PREFIX
                + "_" + machineId
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取 release 操作步骤日志文件
     *
     * @param releaseId releaseId
     * @param id        id
     * @return path
     */
    public static String getReleaseActionLogPath(Long releaseId, Long id) {
        return Const.RELEASE_LOG_DIR
                + "/" + releaseId
                + Const.RELEASE_ACTION_LOG_PREFIX
                + "_" + id
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取 release 产物快照文件
     *
     * @param releaseId releaseId
     * @param distPath  distPath
     * @return path
     */
    public static String getDistSnapshotPath(Long releaseId, String distPath) {
        return "/" + releaseId + "_" + Files1.getFileName(distPath);
    }

    /**
     * 获取 sftp upload 文件路径
     *
     * @param fileToken fileToken
     * @return path
     */
    public static String getSftpUploadFilePath(String fileToken) {
        return Const.UPLOAD_DIR + "/" + fileToken + Const.SWAP_FILE_SUFFIX;
    }

    /**
     * 获取 sftp download 文件路径
     *
     * @param fileToken fileToken
     * @return path
     */
    public static String getSftpDownloadFilePath(String fileToken) {
        return Const.DOWNLOAD_DIR + "/" + fileToken + Const.SWAP_FILE_SUFFIX;
    }

    /**
     * 获取 sftp package 文件路径
     *
     * @param fileToken fileToken
     * @return path
     */
    public static String getSftpPackageFilePath(String fileToken) {
        return Const.PACKAGE_DIR + "/" + fileToken + "." + Const.SUFFIX_ZIP;
    }

    /**
     * 获取环境根目录
     *
     * @param username 用户名
     * @return 目录
     */
    public static String getEnvPath(String username) {
        if (Const.ROOT.equals(username)) {
            return "/" + Const.ROOT + "/" + Const.ORION_OPS + "/";
        } else {
            return "/home/" + username + "/" + Const.ORION_OPS + "/";
        }
    }

    /**
     * 获取秘钥路径
     *
     * @return path
     */
    public static String getSecretKeyPath() {
        return "/" + ObjectIds.next() + Const.SECRET_KEY_SUFFIX;
    }

    /**
     * 获取宿主机环境目录
     *
     * @param path 子目录
     * @return path
     */
    public static String getHostEnvPath(String path) {
        return Systems.HOME_DIR + "/" + Const.ORION_OPS + "/" + path;
    }

}
