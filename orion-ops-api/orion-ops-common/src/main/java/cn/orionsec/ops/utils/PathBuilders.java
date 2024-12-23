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
package cn.orionsec.ops.utils;

import cn.orionsec.kit.lang.id.ObjectIds;
import cn.orionsec.kit.lang.utils.Systems;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.constant.Const;

import java.util.List;

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
     * 获取 terminal 录屏路径
     *
     * @param userId    userId
     * @param machineId machineId
     * @return path
     */
    public static String getTerminalScreenPath(Long userId, Long machineId) {
        return Const.TERMINAL_DIR
                + "/" + userId
                + "_" + machineId
                + "_" + Dates.current(Dates.YMD_HMS2)
                + "." + Const.CAST_SUFFIX;
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
        return Const.EXEC_DIR + suffix
                + "/" + execId
                + "_" + machineId
                + "_" + Dates.current(Dates.YMD_HMS2)
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取应用构建产物文件目录
     *
     * @param buildId buildId
     * @return path
     */
    public static String getBuildBundlePath(Long buildId) {
        return Const.BUILD_DIR + "/" + buildId;
    }

    /**
     * 获取应用构建日志文件
     *
     * @param buildId buildId
     * @return path
     */
    public static String getBuildLogPath(Long buildId) {
        return Const.BUILD_DIR
                + "/" + buildId
                + "/" + Const.BUILD
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取应用构建操作日志文件
     *
     * @param buildId buildId
     * @return path
     */
    public static String getBuildActionLogPath(Long buildId, Long actionId) {
        return Const.BUILD_DIR
                + "/" + buildId
                + "/" + Const.ACTION
                + "_" + actionId
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取应用发布目标机器日志文件
     *
     * @param releaseId releaseId
     * @param machineId 机器id
     * @return path
     */
    public static String getReleaseMachineLogPath(Long releaseId, Long machineId) {
        return Const.RELEASE_DIR
                + "/" + releaseId
                + Const.RELEASE_MACHINE_PREFIX
                + "_" + machineId
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取应用发布操作日志文件
     *
     * @param releaseId releaseId
     * @param machineId machineId
     * @param actionId  actionId
     * @return path
     */
    public static String getReleaseActionLogPath(Long releaseId, Long machineId, Long actionId) {
        return Const.RELEASE_DIR
                + "/" + releaseId
                + Const.RELEASE_MACHINE_PREFIX
                + "_" + machineId
                + "_" + Const.ACTION
                + "_" + actionId
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
     * 获取调度任务机器日志文件
     *
     * @param taskId    taskId
     * @param recordId  recordId
     * @param machineId machineId
     * @return path
     */
    public static String getSchedulerTaskLogPath(Long taskId, Long recordId, Long machineId) {
        return Const.TASK_DIR
                + "/" + taskId
                + "/" + recordId
                + "/" + machineId
                + "." + Const.SUFFIX_LOG;
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
     * 获取导出数据 xlsx 文件路径
     *
     * @param userId userId
     * @param type   type
     * @return path
     */
    public static String getExportDataJsonPath(Long userId, Integer type, String password) {
        return Const.EXPORT_DIR
                + "/" + type
                + "/" + Dates.current(Dates.YMD_HMS2)
                + "-" + userId
                + "-" + password
                + "." + Const.SUFFIX_XLSX;
    }

    /**
     * 获取导入数据 json 文件路径
     *
     * @param userId userId
     * @param type   type
     * @param token  token
     * @return path
     */
    public static String getImportDataJsonPath(Long userId, Integer type, String token) {
        return Const.IMPORT_DIR
                + "/" + type
                + "/" + Dates.current(Dates.YMD_HMS2)
                + "-" + userId
                + "-" + token
                + "." + Const.SUFFIX_JSON;
    }

    /**
     * 获取用户根目录
     *
     * @param username 用户名
     * @return 用户目录
     */
    public static String getHomePath(String username) {
        if (Const.ROOT.equals(username)) {
            return "/" + Const.ROOT;
        } else {
            return "/home/" + username;
        }
    }

    /**
     * 获取密钥路径
     *
     * @return path
     */
    public static String getSecretKeyPath() {
        return "/" + ObjectIds.nextId() + Const.SECRET_KEY_SUFFIX;
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

    /**
     * 获取 sftp 打包临时目录
     *
     * @param username  username
     * @param fileToken fileToken
     * @param paths     paths
     * @return path
     */
    public static String getSftpPackageTempPath(String username, String fileToken, List<String> paths) {
        return PathBuilders.getHomePath(username)
                + "/" + Const.ORION_OPS
                + "/" + Const.PACKAGE
                + "/" + fileToken
                + "/" + Files1.getFileName(paths.get(0))
                + " more files"
                + "." + Const.SUFFIX_ZIP;
    }

    /**
     * 获取插件目录
     *
     * @param username username
     * @return path
     */
    public static String getPluginPath(String username) {
        return PathBuilders.getHomePath(username)
                + "/" + Const.ORION_OPS
                + "/" + Const.PLUGINS;
    }

    /**
     * 获取安装日志路径
     *
     * @param machineId machineId
     * @param app       app
     * @return path
     */
    public static String getInstallLogPath(Long machineId, String app) {
        return Const.INSTALL_DIR
                + "/" + app
                + "/" + machineId
                + "-" + Dates.current(Dates.YMD_HMS2)
                + "." + Const.SUFFIX_LOG;
    }

}
