package com.orion.ops.service.api;

import com.orion.ops.entity.domain.ApplicationPipelineDetailDO;
import com.orion.ops.entity.vo.ApplicationPipelineDetailVO;

import java.util.List;

/**
 * 应用流水线详情服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:53
 */
public interface ApplicationPipelineDetailService {

    /**
     * 通过 pipelineId 查询
     *
     * @param pipelineId pipelineId
     * @return rows
     */
    List<ApplicationPipelineDetailVO> getByPipelineId(Long pipelineId);

    /**
     * 通过 pipelineId 查询
     *
     * @param pipelineId pipelineId
     * @return rows
     */
    List<ApplicationPipelineDetailDO> selectByPipelineId(Long pipelineId);

    /**
     * 通过 pipelineId 查询
     *
     * @param pipelineIdList pipelineIdList
     * @return rows
     */
    List<ApplicationPipelineDetailDO> selectByPipelineIdList(List<Long> pipelineIdList);

    /**
     * 通过 pipelineId 删除
     *
     * @param pipelineId pipelineId
     * @return effect
     */
    Integer deleteByPipelineId(Long pipelineId);

    /**
     * 通过 pipelineId 删除
     *
     * @param pipelineIdList pipelineIdList
     * @return effect
     */
    Integer deleteByPipelineIdList(List<Long> pipelineIdList);

    /**
     * 通过 profileId 删除
     *
     * @param profileId profileId
     * @return effect
     */
    Integer deleteByProfileId(Long profileId);

    /**
     * 通过 appId 删除
     *
     * @param appId appId
     * @return effect
     */
    Integer deleteByAppId(Long appId);

}
