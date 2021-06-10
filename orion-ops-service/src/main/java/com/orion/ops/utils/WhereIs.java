package com.orion.ops.utils;

import com.orion.process.Processes;
import com.orion.utils.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * whereis 工具类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 16:29
 */
@Slf4j
public class WhereIs {

    private WhereIs() {
    }

    private static final String WHERE_IS = "whereis";

    private static final String USR_BIN = "/usr/bin";


    /**
     * whereis 命令
     *
     * @param command command
     * @return path
     */
    public static String whereIs(String command) {
        try {
            String s = Processes.getOutputResultWithDirString(USR_BIN, WHERE_IS, command);
            String[] split = s.split(Strings.SPACE);
            if (split.length == 1) {
                return null;
            }
            return Strings.def(split[1], (String) null);
        } catch (Exception e) {
            log.error("执行whereis失败: command: whereis {}, e: {}", command, e);
            return null;
        }
    }

}
