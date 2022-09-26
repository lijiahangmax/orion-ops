package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.MachineGroupDO;
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

}
