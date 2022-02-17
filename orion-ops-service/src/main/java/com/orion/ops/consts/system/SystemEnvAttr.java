package com.orion.ops.consts.system;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统环境变量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 16:58
 */
@Getter
public enum SystemEnvAttr {

    /**
     * 存放秘钥文件目录
     */
    KEY_PATH("秘钥目录", false),

    /**
     * 存放图片目录
     */
    PIC_PATH("图片目录", false),

    /**
     * 交换目录
     */
    SWAP_PATH("交换目录", false),

    /**
     * 日志目录
     */
    LOG_PATH("日志目录", false),

    /**
     * 临时文件目录
     */
    TEMP_PATH("临时目录", false),

    /**
     * 应用版本仓库目录
     */
    VCS_PATH("应用版本仓库目录", false),

    /**
     * 构建产物目录
     */
    DIST_PATH("构建产物目录", false),

    /**
     * 文件追踪模式 tracker或tail 默认tracker
     */
    TAIL_MODE("文件追踪模式 (tracker/tail)", false),

    /**
     * ip 白名单 (无 value)
     */
    WHITE_IP_LIST("ip 白名单", true),

    /**
     * ip 黑名单 (无 value)
     */
    BLACK_IP_LIST("ip 黑名单", true),

    /**
     * ip 白名单 (无 value)
     */
    ENABLE_IP_FILTER("是否启用 ip 过滤", true),

    /**
     * ip 黑名单 (无 value)
     */
    ENABLE_WHITE_IP_LIST("是否启用 ip 白名单", true),

    ;

    /**
     * key
     */
    private final String key;

    /**
     * 描述
     */
    private final String description;

    private final boolean systemEnv;

    @Setter
    private String value;

    SystemEnvAttr(String description, boolean systemEnv) {
        this.description = description;
        this.systemEnv = systemEnv;
        this.key = this.name().toLowerCase();
    }

    public static List<String> getKeys() {
        return Arrays.stream(values())
                .map(SystemEnvAttr::getKey)
                .collect(Collectors.toList());
    }

    public static SystemEnvAttr of(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.key.equals(key))
                .findFirst()
                .orElse(null);
    }

}
