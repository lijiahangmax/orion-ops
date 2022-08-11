package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationEnvDO;
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
