package com.orion.ops.service.api;

import com.orion.ops.entity.vo.ApplicationPipelineTaskLogVO;

import java.util.List;

/**
 * 应用流水线任务日志服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 20:48
 */
public interface ApplicationPipelineTaskLogService {

    /**
     * 获取日志列表
     *
     * @param taskId 任务id
     * @return rows
     */
    List<ApplicationPipelineTaskLogVO> getLogList(Long taskId);

    /**
     * 通过 taskIdList 删除
     *
     * @param taskIdList taskIdList
     * @return effect
     */
    Integer deleteByTaskIdList(List<Long> taskIdList);

}
