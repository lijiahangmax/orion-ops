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

import cn.orionsec.ops.constant.Const;

/**
 * 终端常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 23:09
 */
public class TerminalConst {

    private TerminalConst() {
    }

    /**
     * 判断终端心跳断开的阀值
     */
    public static final int TERMINAL_CONNECT_DOWN = Const.MS_S_60 + Const.MS_S_15;

    /**
     * terminal symbol
     */
    public static final String TERMINAL = "terminal";

    /**
     * 默认背景色
     */
    public static final String BACKGROUND_COLOR = "#212529";

    /**
     * 默认字体颜色
     */
    public static final String FONT_COLOR = "#FFFFFF";

    /**
     * 默认字体大小
     */
    public static final int FONT_SIZE = 14;

    /**
     * 默认字体名称
     */
    public static final String FONT_FAMILY = "courier-new, courier, monospace";

}
