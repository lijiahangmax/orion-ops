package com.orion.ops.constant.system;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 系统清理类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/17 11:38
 */
@AllArgsConstructor
@Getter
public enum SystemCleanType {

    /**
     * 临时文件
     */
    TEMP_FILE(10, "临时文件"),

    /**
     * 日志文件
     */
    LOG_FILE(20, "日志文件"),

    /**
     * 交换文件
     */
    SWAP_FILE(30, "交换文件"),

    /**
     * 历史产物文件
     */
    DIST_FILE(40, "旧版本构建产物"),

    /**
     * 版本仓库文件
     */
    REPO_FILE(50, "旧版本应用仓库"),

    /**
     * 录屏文件
     */
    SCREEN_FILE(60, "录屏文件"),

    ;

    private final Integer type;

    private final String label;

    public static SystemCleanType of(Integer type) {
        if (type == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.type.equals(type))
                .findFirst()
                .orElse(null);
    }

}
