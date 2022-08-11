package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.dto.MachineMonitorDTO;
import com.orion.ops.entity.query.MachineMonitorQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 机器监控配置表 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-08-01
 */
public interface MachineMonitorDAO extends BaseMapper<MachineMonitorDO> {

    /**
     * 查询列表
     *
     * @param query query
     * @param last  last sql
     * @return rows
     */
    List<MachineMonitorDTO> selectMonitorList(@Param("query") MachineMonitorQuery query, @Param("last") String last);

    /**
     * 查询条数
     *
     * @param query query
     * @return count
     */
    Integer selectMonitorCount(@Param("query") MachineMonitorQuery query);

}
