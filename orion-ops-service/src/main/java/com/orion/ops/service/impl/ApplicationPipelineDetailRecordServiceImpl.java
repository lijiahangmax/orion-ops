package com.orion.ops.service.impl;

import com.orion.ops.dao.ApplicationPipelineDetailRecordDAO;
import com.orion.ops.service.api.ApplicationPipelineDetailRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 应用流水线详情明细服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:56
 */
@Service("applicationPipelineDetailRecordService")
public class ApplicationPipelineDetailRecordServiceImpl implements ApplicationPipelineDetailRecordService {

    @Resource
    private ApplicationPipelineDetailRecordDAO applicationPipelineDetailRecordDAO;

}
