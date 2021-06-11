package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.MachineInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 机器信息表 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-03-29
 */
public interface MachineInfoDAO extends BaseMapper<MachineInfoDO> {

    /**
     * 设置id
     *
     * @param oldId 原id
     * @param newId 新id
     */
    void setId(@Param("oldId") Long oldId, @Param("newId") Long newId);

    /**
     * 设置proxyId为null
     *
     * @param proxyId proxyId
     */
    void setProxyIdWithNull(@Param("proxyId") Long proxyId);

    /**
     * 通过host查询id
     *
     * @param host host
     * @return rows
     */
    List<Long> selectIdByHost(@Param("host") String host);

}
