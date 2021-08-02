package com.orion.ops.consts.tail;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * tail 文件类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/10 18:48
 */
@AllArgsConstructor
@Getter
public enum FileTailType {

    /**
     * 命令执行日志
     */
    EXEC_LOG(10),

    /**
     * tail 列表
     */
    TAIL_LIST(20),

    /**
     * 上线单 宿主机步骤
     */
    RELEASE_HOST(30),

    /**
     * 上线单 目标机器步骤
     */
    RELEASE_STAGE(40),

    ;

    private final Integer type;

    public static FileTailType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (FileTailType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
