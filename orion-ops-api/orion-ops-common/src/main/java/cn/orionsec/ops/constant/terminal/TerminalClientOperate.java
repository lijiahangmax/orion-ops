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
package cn.orionsec.ops.constant.terminal;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 终端操作 client 端
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 22:35
 */
@AllArgsConstructor
@Getter
public enum TerminalClientOperate {

    /**
     * 键入
     */
    KEY("0", true),

    /**
     * 连接
     */
    CONNECT("1", true),

    /**
     * ping
     */
    PING("2", false),

    /**
     * pong
     */
    PONG("3", false),

    /**
     * 更改大小
     */
    RESIZE("4", true),

    /**
     * 键入命令
     */
    COMMAND("5", true),

    /**
     * ctrl + l
     */
    CLEAR("6", false),

    ;

    /**
     * 前缀长度
     */
    public static final int PREFIX_SIZE = 1;

    private final String operate;

    private final boolean hasBody;

    public static TerminalClientOperate of(String operate) {
        for (TerminalClientOperate value : values()) {
            if (value.operate.equals(operate)) {
                return value;
            }
        }
        return null;
    }

}
