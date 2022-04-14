package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationPipelineRecordDO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 应用流水线执行明细 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-04-07
 */
public interface ApplicationPipelineRecordDAO extends BaseMapper<ApplicationPipelineRecordDO> {

    /**
     * 设置定时执行时间为空
     *
     * @param id id
     * @return effect
     */
    Integer setTimedExecTimeNull(@Param("id") Long id);

}
