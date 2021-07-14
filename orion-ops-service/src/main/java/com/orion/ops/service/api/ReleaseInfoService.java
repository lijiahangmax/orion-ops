package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.request.ApplicationReleaseBillRequest;
import com.orion.ops.entity.vo.ReleaseBillDetailVO;
import com.orion.ops.entity.vo.ReleaseBillListVO;
import com.orion.ops.entity.vo.ReleaseBillLogVO;

import java.util.List;

/**
 * 上线单信息查询service
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 17:05
 */
public interface ReleaseInfoService {

    /**
     * 查询部署机器
     *
     * @param releaseId releaseId
     * @return machines
     */
    List<ReleaseMachineDO> getReleaseMachine(Long releaseId);

    /**
     * 查询部署应用环境变量
     *
     * @param releaseId releaseId
     * @return env
     */
    List<ReleaseAppEnvDO> getReleaseAppEnv(Long releaseId);

    /**
     * 查询部署机器环境变量
     *
     * @param releaseId releaseId
     * @return env
     */
    List<ReleaseMachineEnvDO> getReleaseMachineEnv(Long releaseId);

    /**
     * 查询部署机器环境变量
     *
     * @param releaseId releaseId
     * @param machineId machineId
     * @return env
     */
    List<ReleaseMachineEnvDO> getReleaseMachineEnv(Long releaseId, Long machineId);

    /**
     * 查询部署操作
     *
     * @param releaseId releaseId
     * @return action
     */
    List<ReleaseActionDO> getReleaseAction(Long releaseId);

    /**
     * 查询部署操作日志
     *
     * @param releaseActionId releaseActionId
     * @return log
     */
    ReleaseActionLogDO getReleaseActionLog(Long releaseActionId);

    /**
     * 上线单列表
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<ReleaseBillListVO> releaseBillList(ApplicationReleaseBillRequest request);

    /**
     * 上线单详情
     *
     * @param id id
     * @return ReleaseBillDetailVO
     */
    ReleaseBillDetailVO releaseBillDetail(Long id);

    /**
     * 上线单日志
     *
     * @param id id
     * @return ReleaseBillLogVO
     */
    ReleaseBillLogVO releaseBillLog(Long id);

}
