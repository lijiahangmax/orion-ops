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
package cn.orionsec.ops.handler.tail;

import lombok.Data;

/**
 * 文件tail 配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/20 16:34
 */
@Data
public class TailFileHint {

    /**
     * token
     */
    private String token;

    /**
     * 文件
     */
    private String path;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 尾行偏移量
     */
    private Integer offset;

    /**
     * 编码格式
     */
    private String charset;

    /**
     * tail 命令
     */
    private String command;

    /**
     * 延迟时间
     */
    private int delay;

}
