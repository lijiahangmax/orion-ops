package com.orion.ops.service.api;

import com.orion.ops.entity.domain.ApplicationReleaseActionDO;

import java.util.List;

/**
 * <p>
 * 发布单操作步骤 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
public interface ApplicationReleaseActionService {

    /**
     * 获取机器发布操作
     *
     * @param releaseMachineId 发布机器id
     * @return rows
     */
    List<ApplicationReleaseActionDO> getReleaseActionByReleaseMachineId(Long releaseMachineId);

    /**
     * 获取发布操作日志
     *
     * @param id id
     * @return path
     */
    String getReleaseActionLogPath(Long id);

}
