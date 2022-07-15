package com.orion.ops.constant.app;

import com.orion.ops.constant.Const;
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
    GITHUB(10, Const.GITHUB),

    /**
     * gitee
     * <p>
     * username: username
     */
    GITEE(20, Const.GITEE),

    /**
     * gitlab
     * <p>
     * username oauth
     */
    GITLAB(30, Const.GITLAB),

    ;

    private final Integer type;

    private final String label;

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

    public static VcsTokenType of(String label) {
        if (label == null) {
            return null;
        }
        for (VcsTokenType value : values()) {
            if (value.label.equals(label.toLowerCase())) {
                return value;
            }
        }
        return null;
    }

}
