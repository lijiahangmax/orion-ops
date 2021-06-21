package com.orion.ops.consts.tail;

import com.orion.utils.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 宿主机tail 追踪模式
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/10 19:10
 */
@AllArgsConstructor
@Getter
public enum FileTailMode {

    /**
     * 仅宿主机
     *
     * @see com.orion.tail.Tracker
     * @see com.orion.ops.handler.tail.impl.TrackerTailFileHandler
     */
    TRACKER("tracker"),

    /**
     * tail 命令
     * <p>
     * 宿主机 远程机器
     *
     * @see com.orion.ops.handler.tail.impl.ExecTailFileHandler
     */
    TAIL("tail"),

    ;

    String mode;

    public static FileTailMode of(String mode) {
        return of(mode, false);
    }

    public static FileTailMode of(String mode, boolean hostMachine) {
        if (Strings.isBlank(mode)) {
            return hostMachine ? TRACKER : TAIL;
        }
        for (FileTailMode value : values()) {
            if (value.mode.equals(mode)) {
                return value;
            }
        }
        return hostMachine ? TRACKER : TAIL;
    }

}
