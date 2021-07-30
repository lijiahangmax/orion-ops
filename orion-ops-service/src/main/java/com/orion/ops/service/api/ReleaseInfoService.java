package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.domain.ReleaseActionDO;
import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.ops.entity.domain.ReleaseMachineDO;
import com.orion.ops.entity.request.ApplicationReleaseBillRequest;
import com.orion.ops.entity.vo.ReleaseBillDetailVO;
import com.orion.ops.entity.vo.ReleaseBillListVO;

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
     * 查询上线单
     *
     * @param id id
     * @return 上线单
     */
    ReleaseBillDO getReleaseBill(Long id);

    /**
     * 查询最后一个上线单
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 上线单
     */
    ReleaseBillDO getLastReleaseBill(Long appId, Long profileId);

    /**
     * 查询部署机器
     *
     * @param releaseId releaseId
     * @return machines
     */
    List<ReleaseMachineDO> getReleaseMachine(Long releaseId);

    /**
     * 查询部署操作
     *
     * @param releaseId releaseId
     * @return action
     */
    List<ReleaseActionDO> getReleaseAction(Long releaseId);

    /**
     * 查询部署操作
     *
     * @param releaseId releaseId
     * @param machineId machineId
     * @return action
     */
    List<ReleaseActionDO> getReleaseAction(Long releaseId, Long machineId);

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
     * 上线单宿主机日志
     *
     * @param releaseId 上线单id
     * @return log
     */
    String releaseTargetLog(Long releaseId);

    /**
     * 上线单目标机器日志
     *
     * @param releaseMachineId 机器id
     * @return log
     */
    String releaseMachineLog(Long releaseMachineId);

    /**
     * 复制 release 机器
     *
     * @param sourceReleaseId sourceReleaseId
     * @param targetReleaseId targetReleaseId
     */
    void copyReleaseMachine(Long sourceReleaseId, Long targetReleaseId);

    /**
     * 复制 release action
     *
     * @param sourceReleaseId sourceReleaseId
     * @param targetReleaseId targetReleaseId
     */
    void copyReleaseAction(Long sourceReleaseId, Long targetReleaseId);

}
