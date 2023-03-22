package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 机器ssh登陆秘钥 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-03-29
 */
public interface MachineSecretKeyDAO extends BaseMapper<MachineSecretKeyDO> {

    /**
     * 通过名称查询id
     *
     * @param nameList nameList
     * @return rows
     */
    List<MachineSecretKeyDO> selectIdByNameList(@Param("nameList") List<String> nameList);

}
