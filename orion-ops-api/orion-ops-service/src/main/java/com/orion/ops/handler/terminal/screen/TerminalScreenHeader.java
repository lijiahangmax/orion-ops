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
package com.orion.ops.handler.terminal.screen;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * terminal 录屏头
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/26 18:21
 */
@Data
public class TerminalScreenHeader {

    private static final Integer CAST_VERSION = 2;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 标题
     */
    private String title;

    /**
     * cols
     */
    @JSONField(name = "width")
    private Integer cols;

    /**
     * rows
     */
    @JSONField(name = "height")
    private Integer rows;

    /**
     * 开始时间戳
     */
    private Long timestamp;

    /**
     * 环境变量
     */
    private TerminalScreenEnv env;

    public TerminalScreenHeader() {
        this.version = CAST_VERSION;
    }

}
