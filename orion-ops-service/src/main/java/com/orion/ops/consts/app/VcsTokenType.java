package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 仓库令牌类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/19 22:38
 */
@AllArgsConstructor
@Getter
public enum VcsTokenType {

    /**
     * github
     * <p>
     * username: ''
     */
    GITHUB(10),

    /**
     * gitee
     * <p>
     * username: username
     */
    GITEE(20),

    /**
     * gitlab
     * <p>
     * username oauth
     */
    GITLAB(30),

    ;

    private final Integer type;

    public static VcsTokenType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (VcsTokenType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
