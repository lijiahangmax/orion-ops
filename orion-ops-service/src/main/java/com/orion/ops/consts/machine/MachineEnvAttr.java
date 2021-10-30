package com.orion.ops.consts.machine;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机器环境变量key
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/3/29 15:42
 */
@Getter
public enum MachineEnvAttr {

    /**
     * 存放秘钥文件目录
     */
    KEY_PATH(true, false, "秘钥目录"),

    /**
     * 存放图片目录
     */
    PIC_PATH(true, false, "图片目录"),

    /**
     * 临时交换目录
     */
    SWAP_PATH(true, false, "临时交换目录"),

    /**
     * sftp 文件名称编码格式 默认UTF-8
     */
    SFTP_CHARSET(true, true, "sftp 文件名称编码格式"),

    /**
     * 文件追踪模式 tracker或tail 默认tracker
     */
    TAIL_MODE(true, false, "文件追踪模式 tracker或tail"),

    /**
     * 文件追踪偏移量
     */
    TAIL_OFFSET(true, true, "文件追踪偏移量(行)"),

    /**
     * 文件追踪编码格式
     */
    TAIL_CHARSET(true, true, "文件追踪编码格式"),

    /**
     * 日志目录
     */
    LOG_PATH(true, false, "日志目录"),

    /**
     * 临时文件目录
     */
    TEMP_PATH(true, false, "临时目录"),

    /**
     * 宿主机存放部署产物目录
     * 目标集群产物分发的目录
     */
    DIST_PATH(true, true, "宿主机存放部署产物目录 目标集群产物分发目录"),

    ;

    /**
     * key
     */
    private final String key;

    /**
     * 宿主机是否需要
     */
    private final boolean host;

    /**
     * 目标机器是否需要
     */
    private final boolean target;

    /**
     * 描述
     */
    private final String description;

    @Setter
    private String value;

    MachineEnvAttr(boolean host, boolean target, String description) {
        this.host = host;
        this.target = target;
        this.description = description;
        this.key = this.name().toLowerCase();
    }

    public static List<String> getHostKeys() {
        return Arrays.stream(values())
                .filter(MachineEnvAttr::isHost)
                .map(MachineEnvAttr::getKey)
                .collect(Collectors.toList());
    }

    public static List<String> getTargetKeys() {
        return Arrays.stream(values())
                .filter(MachineEnvAttr::isTarget)
                .map(MachineEnvAttr::getKey)
                .collect(Collectors.toList());
    }

    public static MachineEnvAttr of(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.key.equals(key))
                .findFirst()
                .orElse(null);
    }

}
