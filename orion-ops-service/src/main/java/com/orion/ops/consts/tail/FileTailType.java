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
     *
     * @see com.orion.ops.consts.machine.MachineEnvAttr#LOG_PATH
     */
    EXEC_LOG(10, true),

    /**
     * tail 列表
     */
    TAIL_LIST(20, false),

    /**
     * 应用构建日志
     *
     * @see com.orion.ops.consts.machine.MachineEnvAttr#LOG_PATH
     */
    APP_BUILD_LOG(30, true),

    /**
     * 应用发布 目标机器流程日志
     *
     * @see com.orion.ops.consts.machine.MachineEnvAttr#LOG_PATH
     */
    APP_RELEASE_LOG(40, true),

    ;

    private final Integer type;

    /**
     * 是否为本地文件
     */
    private final boolean isLocal;

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
