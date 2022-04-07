package com.orion.ops.controller;

import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.service.api.ApplicationPipelineDetailRecordService;
import com.orion.ops.service.api.ApplicationPipelineRecordService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 应用流水线明细 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:54
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-pipeline-record")
public class ApplicationPipelineRecordController {

    @Resource
    private ApplicationPipelineRecordService applicationPipelineRecordService;

    @Resource
    private ApplicationPipelineDetailRecordService applicationPipelineDetailRecordService;

}
