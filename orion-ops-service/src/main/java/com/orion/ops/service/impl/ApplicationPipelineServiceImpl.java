package com.orion.ops.service.impl;

import com.orion.ops.dao.ApplicationPipelineDAO;
import com.orion.ops.dao.ApplicationPipelineDetailDAO;
import com.orion.ops.service.api.ApplicationPipelineService;

import javax.annotation.Resource;

/**
 * 应用流水线服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:16
 */
public class ApplicationPipelineServiceImpl implements ApplicationPipelineService {

    @Resource
    private ApplicationPipelineDAO applicationPipelineDAO;

    @Resource
    private ApplicationPipelineDetailDAO applicationPipelineDetailDAO;


}
