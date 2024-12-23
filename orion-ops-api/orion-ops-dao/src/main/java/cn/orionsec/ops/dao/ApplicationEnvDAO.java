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

import cn.orionsec.ops.entity.domain.ApplicationEnvDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 应用环境变量 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
public interface ApplicationEnvDAO extends BaseMapper<ApplicationEnvDO> {

    /**
     * 查询一条数据
     *
     * @param appId     appId
     * @param profileId profileId
     * @param key       key
     * @return env
     */
    ApplicationEnvDO selectOneRel(@Param("appId") Long appId, @Param("profileId") Long profileId, @Param("key") String key);

    /**
     * 设置删除
     *
     * @param id      id
     * @param deleted deleted
     * @return effect
     */
    Integer setDeleted(@Param("id") Long id, @Param("deleted") Integer deleted);

}
