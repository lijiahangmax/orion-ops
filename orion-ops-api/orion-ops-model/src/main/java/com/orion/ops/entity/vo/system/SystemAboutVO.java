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
package com.orion.ops.entity.vo.system;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统版本响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/9 13:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "系统信息响应")
public class SystemAboutVO {

    /**
     * orion-kit 版本
     */
    private String orionKitVersion;

    /**
     * orion-ops 版本
     */
    private String orionOpsVersion;

    /**
     * 作者
     */
    private String author;

    /**
     * 作者
     */
    private String authorCn;

}
