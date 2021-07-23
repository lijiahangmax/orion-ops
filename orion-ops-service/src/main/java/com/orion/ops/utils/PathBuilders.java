package com.orion.ops.utils;

import com.orion.ops.consts.Const;
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
     * 获取terminal日志路径
     *
     * @param userId userId
     * @return path
     */
    public static String getTerminalLogPath(Long userId) {
        return Const.TERMINAL_LOG_DIR
                + "/" + Dates.current(Dates.YMDHMS2)
                + "_" + userId
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取exec日志路径
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
                + "_" + Dates.current(Dates.YMDHMS2)
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取release 宿主机日志文件
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
     * 获取release 目标机器日志文件
     *
     * @param releaseId releaseId
     * @param machineId 目标机器
     * @return path
     */
    public static String getReleaseTargetLogPath(Long releaseId, Long machineId) {
        return Const.RELEASE_LOG_DIR
                + "/" + releaseId
                + Const.RELEASE_TARGET_LOG_PREFIX
                + "_" + machineId
                + "." + Const.SUFFIX_LOG;
    }

    /**
     * 获取release 操作步骤日志文件
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

}
