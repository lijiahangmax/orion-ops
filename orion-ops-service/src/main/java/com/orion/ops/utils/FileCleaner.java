package com.orion.ops.utils;

import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.system.SystemCleanType;
import com.orion.ops.constant.system.SystemEnvAttr;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * 文件清理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 3:08
 */
@Slf4j
public class FileCleaner {

    private FileCleaner() {
    }

    /**
     * 清理所有
     */
    public static void cleanAll() {
        Date threshold = Dates.stream().subDay(Integer.parseInt(SystemEnvAttr.FILE_CLEAN_THRESHOLD.getValue())).get();
        long thresholdTime = threshold.getTime();
        log.info("自动清理文件-阈值 {}", Dates.format(threshold));
        long releasedBytes = 0L;
        // 清理临时文件
        releasedBytes += deletePathFiles(thresholdTime, new File(SystemEnvAttr.TEMP_PATH.getValue()));
        // 日志文件
        releasedBytes += deletePathFiles(thresholdTime, new File(SystemEnvAttr.LOG_PATH.getValue()));
        // 交换文件
        releasedBytes += deletePathFiles(thresholdTime, new File(SystemEnvAttr.SWAP_PATH.getValue()));
        // 构建产物
        releasedBytes += deletePathFiles(thresholdTime, new File(SystemEnvAttr.DIST_PATH.getValue()));
        // // vcs产物 文件太多会 oom
        // File vcsPath = new File(SystemEnvAttr.VCS_PATH.getValue());
        // List<File> vcsPaths = Files1.listFilesFilter(vcsPath, (f, n) -> f.isDirectory() && !Const.EVENT.equals(n), false, true);
        // for (File vcs : vcsPaths) {
        //     releasedBytes += deletePathFiles(thresholdTime, vcs);
        // }
        log.info("自动清理文件-释放 {}", Files1.getSize(releasedBytes));
    }

    /**
     * 清理目录
     *
     * @param cleanType cleanType
     */
    public static void cleanDir(SystemCleanType cleanType) {
        Date threshold = Dates.stream().subDay(Integer.parseInt(SystemEnvAttr.FILE_CLEAN_THRESHOLD.getValue())).get();
        long thresholdTime = threshold.getTime();
        long releasedBytes = 0L;
        log.info("手动清理文件-阈值 {}", Dates.format(threshold));
        switch (cleanType) {
            case TEMP_FILE:
                // 清理临时文件
                releasedBytes += deletePathFiles(thresholdTime, new File(SystemEnvAttr.TEMP_PATH.getValue()));
                break;
            case LOG_FILE:
                // 日志文件
                releasedBytes += deletePathFiles(thresholdTime, new File(SystemEnvAttr.LOG_PATH.getValue()));
                break;
            case SWAP_FILE:
                // 交换文件
                releasedBytes += deletePathFiles(thresholdTime, new File(SystemEnvAttr.SWAP_PATH.getValue()));
                break;
            case DIST_FILE:
                // 构建产物
                releasedBytes += deletePathFiles(thresholdTime, new File(SystemEnvAttr.DIST_PATH.getValue()));
                break;
            case VCS_FILE:
                // vcs产物
                File vcsPath = new File(SystemEnvAttr.VCS_PATH.getValue());
                List<File> vcsPaths = Files1.listFilesFilter(vcsPath, (f, n) -> f.isDirectory() && !Const.EVENT.equals(n), false, true);
                for (File vcs : vcsPaths) {
                    releasedBytes += deletePathFiles(thresholdTime, vcs);
                }
                break;
            default:
        }
        log.info("手动清理文件-释放 {}", Files1.getSize(releasedBytes));
    }

    /**
     * 删除文件
     *
     * @param thresholdTime 阈值
     * @param path          文件夹
     * @return size
     */
    private static long deletePathFiles(long thresholdTime, File path) {
        List<File> files = Files1.listFilesFilter(path, (p, n) -> checkLessThanThreshold(thresholdTime, p, n), true, true);
        long size = files.stream().mapToLong(File::length).sum();
        files.stream().filter(File::isFile).forEach(Files1::delete);
        files.stream().filter(File::isDirectory).forEach(Files1::delete);
        files.clear();
        return size;
    }

    /**
     * 检查文件是否超过阈值
     *
     * @param thresholdTime 阈值
     * @param file          文件
     * @param s             文件名
     * @return 是否超过
     */
    private static boolean checkLessThanThreshold(long thresholdTime, File file, String s) {
        return file.lastModified() <= thresholdTime;
    }

}
