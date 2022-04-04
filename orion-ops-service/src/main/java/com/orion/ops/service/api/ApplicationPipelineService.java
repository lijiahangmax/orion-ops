package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.request.ApplicationPipelineRequest;
import com.orion.ops.entity.vo.ApplicationPipelineVO;

import java.util.List;

/**
 * 应用流水线服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:16
 */
public interface ApplicationPipelineService {

    /**
     * 添加流水线
     *
     * @param request request
     * @return request
     */
    Long addPipeline(ApplicationPipelineRequest request);

    /**
     * 更新流水线
     *
     * @param request request
     * @return effect
     */
    Integer updatePipeline(ApplicationPipelineRequest request);

    /**
     * 流水线列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationPipelineVO> listPipeline(ApplicationPipelineRequest request);

    /**
     * 获取流水线详情
     *
     * @param id id
     * @return row
     */
    ApplicationPipelineVO getPipeline(Long id);

    /**
     * 删除流水线
     *
     * @param idList idList
     * @return effect
     */
    Integer deletePipeline(List<Long> idList);

    /**
     * 通过 profileId 删除
     *
     * @param profileId profileId
     * @return effect
     */
    Integer deleteByProfileId(Long profileId);

}
