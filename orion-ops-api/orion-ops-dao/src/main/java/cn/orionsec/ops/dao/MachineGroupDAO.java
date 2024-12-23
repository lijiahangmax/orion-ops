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
package cn.orionsec.ops.dao;

import cn.orionsec.ops.entity.domain.MachineGroupDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 机器分组 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-09-23
 */
public interface MachineGroupDAO extends BaseMapper<MachineGroupDO> {

    /**
     * 增加排序值
     *
     * @param parentId    parentId
     * @param greaterSort > sort
     * @return effect
     */
    Integer incrementSort(@Param("parentId") Long parentId, @Param("greaterSort") Integer greaterSort);

    /**
     * 增加最大 sort
     *
     * @param parentId parentId
     * @return sort
     */
    Integer getMaxSort(@Param("parentId") Long parentId);

}
