package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationActionDO;
import com.orion.ops.entity.dto.ApplicationActionConfigDTO;
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
