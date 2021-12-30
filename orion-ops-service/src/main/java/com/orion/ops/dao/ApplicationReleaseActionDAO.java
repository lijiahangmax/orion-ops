package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationReleaseActionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 发布单操作步骤 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
public interface ApplicationReleaseActionDAO extends BaseMapper<ApplicationReleaseActionDO> {

    /**
     * 查询状态
     *
     * @param releaseMachineId releaseMachineId
     * @return rows
     */
    List<ApplicationReleaseActionDO> selectReleaseActionStatusByMachineId(@Param("releaseMachineId") Long releaseMachineId);

    /**
     * 查询状态
     *
     * @param releaseMachineIdList releaseMachineIdList
     * @return rows
     */
    List<ApplicationReleaseActionDO> selectReleaseActionStatusByMachineIdList(@Param("releaseMachineIdList") List<Long> releaseMachineIdList);

}
