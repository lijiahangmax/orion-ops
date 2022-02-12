package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用操作日志 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-11
 */
public interface ApplicationActionLogDAO extends BaseMapper<ApplicationActionLogDO> {

    /**
     * 通过 id 查询状态信息
     *
     * @param id id
     * @return row
     */
    ApplicationActionLogDO selectStatusInfoById(@Param("id") Long id);

    /**
     * 通过 id 查询状态信息
     *
     * @param idList idList
     * @return rows
     */
    List<ApplicationActionLogDO> selectStatusInfoByIdList(@Param("idList") List<Long> idList);

    /**
     * 通过 relId 查询状态信息
     *
     * @param relId     relId
     * @param stageType stageType
     * @return rows
     */
    List<ApplicationActionLogDO> selectStatusInfoByRelId(@Param("relId") Long relId, @Param("stageType") Integer stageType);

    /**
     * 通过 relId 查询状态信息
     *
     * @param relIdList relIdList
     * @param stageType stageType
     * @return rows
     */
    List<ApplicationActionLogDO> selectStatusInfoByRelIdList(@Param("relIdList") List<Long> relIdList, @Param("stageType") Integer stageType);

}
