package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.SystemEnvDO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 系统环境变量 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-15
 */
public interface SystemEnvDAO extends BaseMapper<SystemEnvDO> {

    /**
     * 查询一条数据
     *
     * @param key key
     * @return env
     */
    SystemEnvDO selectOneRel(@Param("key") String key);

    /**
     * 设置删除
     *
     * @param id      id
     * @param deleted deleted
     * @return effect
     */
    Integer setDeleted(@Param("id") Long id, @Param("deleted") Integer deleted);

}
