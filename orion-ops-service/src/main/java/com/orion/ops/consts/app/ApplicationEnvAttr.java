package com.orion.ops.consts.app;

import lombok.Getter;

import java.util.Arrays;

/**
 * 应用env常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:29
 */
@Getter
public enum ApplicationEnvAttr {

    /**
     * 构建产物路径
     */
    BUNDLE_PATH("构建产物路径 (绝对/相对)"),

    /**
     * 构建序列号
     */
    BUILD_SEQ("构建序列号 (自增)"),

    ;

    /**
     * key
     */
    private final String key;

    /**
     * 描述
     */
    private final String description;

    ApplicationEnvAttr(String description) {
        this.description = description;
        this.key = this.name().toLowerCase();
    }

    public static ApplicationEnvAttr of(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.key.equals(key))
                .findFirst()
                .orElse(null);
    }

}
