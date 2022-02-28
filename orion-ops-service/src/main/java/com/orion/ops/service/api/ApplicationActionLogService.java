package com.orion.ops.service.api;

import com.orion.ops.consts.app.StageType;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.ops.entity.vo.ApplicationActionLogVO;

import java.util.List;

/**
 * 应用操作日志服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/11 16:41
 */
public interface ApplicationActionLogService {

    /**
     * 获取操作执行日志
     *
     * @param relId     relId
     * @param stageType stageType
     * @return rows
     */
    List<ApplicationActionLogVO> getActionLogsByRelId(Long relId, StageType stageType);

    /**
     * 删除
     *
     * @param relId     relId
     * @param stageType stageType
     * @return effect
     */
    Integer deleteByRelId(Long relId, StageType stageType);

    /**
     * 通过 relId 查询 action
     *
     * @param relId     relId
     * @param stageType stageType
     * @return action
     */
    List<ApplicationActionLogDO> selectActionByRelId(Long relId, StageType stageType);

    /**
     * 通过 relIdList 查询 action
     *
     * @param relIdList relIdList
     * @param stageType stageType
     * @return action
     */
    List<ApplicationActionLogDO> selectActionByRelIdList(List<Long> relIdList, StageType stageType);

    /**
     * 更新 action
     *
     * @param record record
     */
    void updateActionById(ApplicationActionLogDO record);

    /**
     * 获取操作执行日志路径
     *
     * @param id id
     * @return path
     */
    String getActionLogPath(Long id);

    /**
     * 设置操作状态
     *
     * @param relId     relId
     * @param stageType stageType
     */
    void resetActionStatus(Long relId, StageType stageType);

}
