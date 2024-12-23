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

import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.system.SystemCleanType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
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
        // 应用仓库 文件太多会 oom
        // File repoPath = new File(SystemEnvAttr.REPO_PATH.getValue());
        // List<File> repoPaths = Files1.listFilesFilter(repoPath, (f, n) -> f.isDirectory() && !Const.EVENT.equals(n), false, true);
        // for (File repo : repoPaths) {
        //     releasedBytes += deletePathFiles(thresholdTime, repo);
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
            case REPO_FILE:
                // 应用仓库
                File repoPath = new File(SystemEnvAttr.REPO_PATH.getValue());
                List<File> repoPaths = Files1.listFilesFilter(repoPath, f -> f.isDirectory() && !Const.EVENT.equals(f.getName()), false, true);
                for (File repo : repoPaths) {
                    releasedBytes += deletePathFiles(thresholdTime, repo);
                }
                break;
            case SCREEN_FILE:
                // 录屏文件
                releasedBytes += deletePathFiles(thresholdTime, new File(SystemEnvAttr.SCREEN_PATH.getValue()));
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
        List<File> files = Files1.listFilesFilter(path, p -> checkLessThanThreshold(thresholdTime, p), true, true);
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
     * @return 是否超过
     */
    private static boolean checkLessThanThreshold(long thresholdTime, File file) {
        return file.lastModified() <= thresholdTime;
    }

}
