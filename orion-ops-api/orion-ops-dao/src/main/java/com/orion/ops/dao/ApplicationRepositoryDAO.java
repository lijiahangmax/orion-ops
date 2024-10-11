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
package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationRepositoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用版本仓库 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-11-26
 */
public interface ApplicationRepositoryDAO extends BaseMapper<ApplicationRepositoryDO> {

    /**
     * 通过id查询名称
     *
     * @param idList idList
     * @return rows
     */
    List<ApplicationRepositoryDO> selectNameByIdList(@Param("idList") List<Long> idList);

    /**
     * 通过名称查询id
     *
     * @param nameList nameList
     * @return rows
     */
    List<ApplicationRepositoryDO> selectIdByNameList(@Param("nameList") List<String> nameList);

}
