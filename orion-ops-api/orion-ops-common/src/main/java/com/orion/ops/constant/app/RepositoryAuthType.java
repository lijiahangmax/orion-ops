package com.orion.ops.constant.app;

import com.orion.ops.constant.CnConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 仓库认证方式
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/19 22:37
 */
@AllArgsConstructor
@Getter
public enum RepositoryAuthType {

    /**
     * 密码
     */
    PASSWORD(10, CnConst.PASSWORD),

    /**
     * 私人令牌
     */
    TOKEN(20, CnConst.TOKEN),

    ;

    private final Integer type;

    private final String label;

    public static RepositoryAuthType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (RepositoryAuthType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static RepositoryAuthType of(String label) {
        if (label == null) {
            return null;
        }
        for (RepositoryAuthType value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        return null;
    }

}
