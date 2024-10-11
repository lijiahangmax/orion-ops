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
package com.orion.ops.constant.command;

import com.orion.ops.constant.env.EnvConst;

/**
 * 命令常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 11:21
 */
public class CommandConst {

    private CommandConst() {
    }

    public static final String TAIL_FILE_DEFAULT = "tail -f -n "
            + EnvConst.getReplaceVariable(EnvConst.OFFSET)
            + " '" + EnvConst.getReplaceVariable(EnvConst.FILE)
            + "'";

    public static final String SCP_TRANSFER_DEFAULT = "scp \""
            + EnvConst.getReplaceVariable(EnvConst.BUNDLE_PATH)
            + "\" " + EnvConst.getReplaceVariable(EnvConst.TARGET_USERNAME)
            + "@" + EnvConst.getReplaceVariable(EnvConst.TARGET_HOST)
            + ":\"" + EnvConst.getReplaceVariable(EnvConst.TRANSFER_PATH)
            + "\"";

}
