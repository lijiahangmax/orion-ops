package com.orion.ops.service.api;

import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.ops.entity.vo.ApplicationPipelineTaskDetailVO;

import java.util.List;

/**
 * 应用流水线任务详情服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:55
 */
public interface ApplicationPipelineTaskDetailService {

    /**
     * 获取流水线详情明细
     *
     * @param taskId taskId
     * @return rows
     */
    List<ApplicationPipelineTaskDetailVO> getTaskDetails(Long taskId);

    /**
     * 获取流水线详情明细
     *
     * @param taskId taskId
     * @return rows
     */
    List<ApplicationPipelineTaskDetailDO> selectTaskDetails(Long taskId);

    /**
     * 获取流水线详情明细
     *
     * @param taskIdList taskIdList
     * @return rows
     */
    List<ApplicationPipelineTaskDetailDO> selectTaskDetails(List<Long> taskIdList);

    /**
     * 通过 taskId 删除详情明细
     *
     * @param taskId taskId
     * @return effect
     */
    Integer deleteByTaskId(Long taskId);

    /**
     * 通过 taskId 删除详情明细
     *
     * @param taskIdList taskIdList
     * @return effect
     */
    Integer deleteByTaskIdList(List<Long> taskIdList);

}
