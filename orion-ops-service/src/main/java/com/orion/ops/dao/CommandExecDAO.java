package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.CommandExecDO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 命令执行表 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-06-04
 */
public interface CommandExecDAO extends BaseMapper<CommandExecDO> {

    /**
     * 通过 id 查询 status
     *
     * @param id id
     * @return status
     */
    Integer selectStatusById(@Param("id") Long id);

}
