package com.orion.ops.consts.machine;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 目标机器机器环境变量key
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/3/29 15:42
 */
@Getter
public enum MachineTargetEnvAttr {

    /**
     * sftp 文件名称编码格式 默认UTF-8
     */
    SFTP_CHARSET("SFTP 文件名称编码格式"),

    /**
     * 文件追踪偏移量
     */
    TAIL_OFFSET("文件追踪偏移量(行)"),

    /**
     * 文件追踪编码格式
     */
    TAIL_CHARSET("文件追踪编码格式"),

    ;

    /**
     * key
     */
    private final String key;

    /**
     * 描述
     */
    private final String description;

    MachineTargetEnvAttr(String description) {
        this.description = description;
        this.key = this.name().toLowerCase();
    }

    public static List<String> getKeys() {
        return Arrays.stream(values())
                .map(MachineTargetEnvAttr::getKey)
                .collect(Collectors.toList());
    }

    public static MachineTargetEnvAttr of(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.key.equals(key))
                .findFirst()
                .orElse(null);
    }

}
