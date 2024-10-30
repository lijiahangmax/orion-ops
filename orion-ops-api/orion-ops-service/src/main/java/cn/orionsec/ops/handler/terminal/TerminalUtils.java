/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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
package cn.orionsec.ops.handler.terminal;

import cn.orionsec.ops.entity.dto.terminal.TerminalConnectDTO;
import cn.orionsec.ops.entity.dto.terminal.TerminalSizeDTO;
import com.orion.lang.utils.Strings;

/**
 * 终端工具类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:30
 */
public class TerminalUtils {

    private TerminalUtils() {
    }

    /**
     * 解析连接参数
     * <p>
     * .e.g cols|rows|loginToken
     *
     * @param body body
     * @return connect
     */
    public static TerminalConnectDTO parseConnectBody(String body) {
        String[] arr = body.split("\\|");
        if (arr.length != 3) {
            return null;
        }
        // 解析 size
        if (!Strings.isInteger(arr[0]) || !Strings.isInteger(arr[1])) {
            return null;
        }
        TerminalConnectDTO connect = new TerminalConnectDTO();
        connect.setCols(Integer.parseInt(arr[0]));
        connect.setRows(Integer.parseInt(arr[1]));
        connect.setLoginToken(arr[2]);
        return connect;
    }

    /**
     * 解析修改大小参数
     * <p>
     * .e.g cols|rows
     *
     * @param body body
     * @return size
     */
    public static TerminalSizeDTO parseResizeBody(String body) {
        String[] arr = body.split("\\|");
        if (arr.length != 2) {
            return null;
        }
        // 解析 size
        if (!Strings.isInteger(arr[0]) || !Strings.isInteger(arr[1])) {
            return null;
        }
        TerminalSizeDTO size = new TerminalSizeDTO();
        size.setCols(Integer.parseInt(arr[0]));
        size.setRows(Integer.parseInt(arr[1]));
        return size;
    }

}
