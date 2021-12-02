package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.MachineEnvDO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 机器环境变量 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-03-29
 */
public interface MachineEnvDAO extends BaseMapper<MachineEnvDO> {

    /**
     * 查询一条数据
     *
     * @param machineId machineId
     * @param key       key
     * @return env
     */
    MachineEnvDO selectOneRel(@Param("machineId") Long machineId, @Param("key") String key);

    /**
     * 设置删除
     *
     * @param id      id
     * @param deleted deleted
     * @return effect
     */
    Integer setDeleted(@Param("id") Long id, @Param("deleted") Integer deleted);

}
