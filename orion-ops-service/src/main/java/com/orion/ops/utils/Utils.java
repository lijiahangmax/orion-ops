package com.orion.ops.utils;

import com.orion.id.UUIds;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.io.FileReaders;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.net.IPs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 工具类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 9:28
 */
public class Utils {

    private Utils() {
    }

    /**
     * 获取复制后缀
     *
     * @return suffix
     */
    public static String getCopySuffix() {
        return getSymbolSuffix(Const.COPY);
    }

    /**
     * 获取后缀
     *
     * @param symbol symbol
     * @return suffix
     */
    public static String getSymbolSuffix(String symbol) {
        return " - " + symbol + Strings.SPACE + UUIds.random32().substring(0, 5).toUpperCase();
    }

    /**
     * 获取file 尾行数据
     *
     * @param path path
     * @return lines
     */
    public static String getFileTailLine(String path) {
        RandomAccessFile reader = null;
        try {
            if (Strings.isBlank(path)) {
                return Strings.EMPTY;
            }
            File file = new File(path);
            // 文件不存在
            if (!Files1.isFile(file)) {
                return Strings.EMPTY;
            }
            // 行数
            Integer offset = SpringHolder.getBean(MachineEnvService.class).getTailOffset(Const.HOST_MACHINE_ID);
            // 获取偏移量
            reader = Files1.openRandomAccess(file, Const.ACCESS_R);
            return FileReaders.readTailLines(reader, offset);
        } catch (IOException e) {
            throw Exceptions.ioRuntime(e);
        } finally {
            Streams.close(reader);
        }
    }

    /**
     * 检查是否是合法 ip 配置行
     *
     * @param line line
     * @return 是否合法
     */
    public static boolean validIpLine(String line) {
        // 普通ip
        line = line.trim();
        if (!line.contains(Const.SLASH)) {
            return IPs.isIpv4(line);
        }
        return validIpRange(line);
    }

    /**
     * 检查是否为合法 ip 区间
     *
     * @param range range
     * @return 是否合法
     */
    public static boolean validIpRange(String range) {
        // ip区间
        String[] split = range.split(Const.SLASH);
        String first = split[0];
        String last = split[1];
        if (split.length != 2) {
            return false;
        }
        // 检查第一段
        if (!IPs.isIpv4(first)) {
            return false;
        }
        // 尾ip
        if (!last.contains(Const.DOT)) {
            last = first.substring(0, first.lastIndexOf(Const.DOT)) + Const.DOT + last;
        }
        return IPs.isIpv4(last);
    }

    /**
     * 检查 ip 是否在范围内
     *
     * @param ip     ip
     * @param filter filter
     * @return 是否在范围内
     */
    public static boolean checkIpIn(String ip, String filter) {
        filter = filter.trim();
        // 单个ip
        if (!filter.contains(Const.SLASH)) {
            return ip.equals(filter);
        }
        // ip区间
        String[] split = filter.split(Const.SLASH);
        String first = split[0];
        String last = split[1];
        // 尾ip
        if (!last.contains(Const.DOT)) {
            last = first.substring(0, first.lastIndexOf(Const.DOT)) + Const.DOT + last;
        }
        return IPs.ipInRange(first, last, ip);
    }

    /**
     * 获取事件仓库路径
     *
     * @param id id
     * @return 路径
     */
    public static String getVcsEventDir(Long id) {
        return Files1.getPath(SystemEnvAttr.VCS_PATH.getValue(), Const.EVENT_DIR + "/" + id);
    }

}
