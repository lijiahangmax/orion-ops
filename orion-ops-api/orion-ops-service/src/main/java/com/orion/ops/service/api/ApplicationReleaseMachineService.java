package com.orion.ops.service.api;

import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;

import java.util.List;

/**
 * <p>
 * 发布任务机器表 服务类
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

    /**
     * 查询发布机器id
     *
     * @param releaseIdList releaseIdList
     * @return rows
     */
    List<Long> getReleaseMachineIdList(List<Long> releaseIdList);

    /**
     * 通过 releaseId 删除
     *
     * @param releaseId releaseId
     * @return effect
     */
    Integer deleteByReleaseId(Long releaseId);

    /**
     * 获取发布机器日志
     *
     * @param id id
     * @return 日志路径
     */
    String getReleaseMachineLogPath(Long id);

}
