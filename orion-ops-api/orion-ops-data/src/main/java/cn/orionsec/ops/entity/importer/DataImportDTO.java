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
package cn.orionsec.ops.entity.importer;

import cn.orionsec.ops.constant.ImportType;
import cn.orionsec.ops.entity.vo.data.DataImportCheckVO;
import lombok.Data;

import java.util.Date;

/**
 * 数据导入对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 17:42
 */
@Data
public class DataImportDTO {

    /**
     * token
     */
    private String importToken;

    /**
     * 类型
     *
     * @see ImportType
     */
    private Integer type;

    /**
     * 导入数据 json
     */
    private String data;

    /**
     * 检查数据
     */
    private DataImportCheckVO check;

    /**
     * 导入用户id 手动
     */
    private Long userId;

    /**
     * 导入用户名称 手动
     */
    private String userName;

    /**
     * 导入时间 手动
     */
    private Date importTime;

}
