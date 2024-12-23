/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.constant.tail;

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
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
     * @see cn.orionsec.ops.tail.Tracker
     * @see cn.orionsec.ops.handler.tail.impl.TrackerTailFileHandler
     */
    TRACKER("tracker"),

    /**
     * tail 命令
     * <p>
     * 宿主机 远程机器
     *
     * @see cn.orionsec.ops.handler.tail.impl.ExecTailFileHandler
     */
    TAIL("tail"),

    ;

    private final String mode;

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

    /**
     * 获取宿主机 tailMode
     *
     * @return tailMode
     */
    public static String getHostTailMode() {
        String mode = SystemEnvAttr.TAIL_MODE.getValue();
        return of(mode, true).getMode();
    }

}
