package com.orion.ops.utils;

import com.orion.id.UUIds;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.io.FileReaders;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;

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
            String off = MachineEnvAttr.TAIL_OFFSET.getValue();
            int offset;
            if (Strings.isNumber(off)) {
                offset = Integer.parseInt(off);
            } else {
                offset = Const.TAIL_OFFSET_LINE;
            }
            // 获取偏移量
            reader = Files1.openRandomAccess(file, Const.ACCESS_R);
            return FileReaders.readTailLines(reader, offset);
        } catch (IOException e) {
            throw Exceptions.ioRuntime(e);
        } finally {
            Streams.close(reader);
        }
    }

}
