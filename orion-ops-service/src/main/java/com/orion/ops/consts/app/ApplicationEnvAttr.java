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
     * 版本控制根目录
     */
    VCS_ROOT_PATH("版本控制根目录 (.git)"),

    /**
     * 应用代码目录
     */
    VCS_CODE_PATH("应用代码目录"),

    /**
     * 版本管理工具 git
     *
     * @see VcsType
     */
    VCS_TYPE("版本管理工具"),

    /**
     * 构建产物目录
     */
    DIST_PATH("构建产物文件路径"),

    ;

    /**
     * key
     */
    String key;

    /**
     * 描述
     */
    String description;

    ApplicationEnvAttr(String description) {
        this.description = description;
        this.key = this.name().toLowerCase();
    }

    public static ApplicationEnvAttr of(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.key.equals(key)).findFirst()
                .orElse(null);
    }

}
