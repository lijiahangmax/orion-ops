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
     * 文件追踪模式 tracker或tail 默认tracker
     */
    TAIL_MODE(true, false, "文件追踪模式 tracker或tail"),

    /**
     * 文件追踪偏移量
     */
    TAIL_OFFSET(true, true, "文件追踪偏移量(byte)"),

    /**
     * 日志目录
     */
    LOG_PATH(true, true, "日志目录"),

    /**
     * 临时文件目录
     */
    TEMP_PATH(true, true, "临时目录"),

    /**
     * 宿主机存放部署产物目录
     * 目标集群产物分发的目录
     */
    DIST_PATH(true, true, "产物目录"),

    ;

    /**
     * 宿主机是否需要
     */
    boolean host;

    /**
     * 目标机器是否需要
     */
    boolean target;

    /**
     * 描述
     */
    String description;

    @Setter
    String value;

    MachineEnvAttr(boolean host, boolean target, String description) {
        this.host = host;
        this.target = target;
        this.description = description;
    }

    public static List<String> getHostKeys() {
        return Arrays.stream(values())
                .filter(MachineEnvAttr::isHost)
                .map(MachineEnvAttr::name)
                .collect(Collectors.toList());
    }

    public static List<String> getTargetKeys() {
        return Arrays.stream(values())
                .filter(MachineEnvAttr::isTarget)
                .map(MachineEnvAttr::name)
                .collect(Collectors.toList());
    }

    public static MachineEnvAttr of(String key) {
        return Arrays.stream(values())
                .filter(a -> a.name().equals(key)).findFirst()
                .orElse(null);
    }

}
