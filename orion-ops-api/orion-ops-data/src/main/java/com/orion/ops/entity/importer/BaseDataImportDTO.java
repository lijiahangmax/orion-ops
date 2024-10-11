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
package com.orion.ops.entity.importer;

import lombok.Data;

/**
 * 导入数据 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 15:27
 */
@Data
public class BaseDataImportDTO {

    /**
     * 非法信息 非法操作
     */
    private String illegalMessage;

    /**
     * 数据id 更新操作
     */
    private Long id;

}
