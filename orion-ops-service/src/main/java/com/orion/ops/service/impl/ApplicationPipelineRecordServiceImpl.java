package com.orion.ops.service.impl;

import com.orion.ops.dao.ApplicationPipelineDetailRecordDAO;
import com.orion.ops.dao.ApplicationPipelineRecordDAO;
import com.orion.ops.service.api.ApplicationPipelineDetailRecordService;
import com.orion.ops.service.api.ApplicationPipelineRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 应用流水线明细服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:57
 */
@Service("applicationPipelineRecordService")
public class ApplicationPipelineRecordServiceImpl implements ApplicationPipelineRecordService {

    @Resource
    private ApplicationPipelineRecordDAO applicationPipelineRecordDAO;

    @Resource
    private ApplicationPipelineDetailRecordDAO applicationPipelineDetailRecordDAO;

    @Resource
    private ApplicationPipelineDetailRecordService applicationPipelineDetailRecordService;

}
