package com.orion.ops.service.api;

import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;

import java.util.List;

/**
 * <p>
 * 发布单机器表 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
public interface ApplicationReleaseMachineService {

    /**
     * 查询发布机器
     *
     * @param releaseId releaseId
     * @return rows
     */
    List<ApplicationReleaseMachineDO> getReleaseMachines(Long releaseId);

    /**
     * 查询发布机器
     *
     * @param releaseIdList releaseIdList
     * @return rows
     */
    List<ApplicationReleaseMachineDO> getReleaseMachines(List<Long> releaseIdList);

}
