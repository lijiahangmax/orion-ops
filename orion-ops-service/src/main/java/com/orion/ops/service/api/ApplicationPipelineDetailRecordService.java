package com.orion.ops.service.api;

import com.orion.ops.entity.domain.ApplicationPipelineDetailRecordDO;
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
     * @param recordId recordId
     * @return rows
     */
    List<ApplicationPipelineDetailRecordVO> getRecordDetails(Long recordId);

    /**
     * 获取流水线详情明细
     *
     * @param recordId recordId
     * @return rows
     */
    List<ApplicationPipelineDetailRecordDO> selectRecordDetails(Long recordId);

    /**
     * 通过 recordId 删除详情明细
     *
     * @param recordId recordId
     * @return effect
     */
    Integer deleteByRecordId(Long recordId);

    /**
     * 通过 recordId 删除详情明细
     *
     * @param recordIdList recordIdList
     * @return effect
     */
    Integer deleteByRecordIdList(List<Long> recordIdList);

}
