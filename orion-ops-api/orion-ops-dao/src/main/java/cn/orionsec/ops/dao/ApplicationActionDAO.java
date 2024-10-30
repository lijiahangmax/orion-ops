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
package cn.orionsec.ops.dao;

import cn.orionsec.ops.entity.domain.ApplicationActionDO;
import cn.orionsec.ops.entity.dto.ApplicationActionConfigDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用执行块 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-07
 */
public interface ApplicationActionDAO extends BaseMapper<ApplicationActionDO> {

    /**
     * 获取应用是否已配置
     *
     * @param profileId profileId
     * @param appIdList appIdList
     * @return rows
     */
    List<ApplicationActionConfigDTO> getAppIsConfig(@Param("profileId") Long profileId, @Param("appIdList") List<Long> appIdList);

}
