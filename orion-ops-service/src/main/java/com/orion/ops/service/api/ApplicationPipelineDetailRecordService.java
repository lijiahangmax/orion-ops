package com.orion.ops.service.api;

import com.orion.ops.entity.vo.ApplicationPipelineDetailRecordVO;

import java.util.List;

/**
 * 应用流水线详情明细服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:55
 */
public interface ApplicationPipelineDetailRecordService {

    /**
     * 获取流水线详情明细
     *
     * @param id id
     * @return 明细
     */
    List<ApplicationPipelineDetailRecordVO> getRecordDetails(Long id);

}
