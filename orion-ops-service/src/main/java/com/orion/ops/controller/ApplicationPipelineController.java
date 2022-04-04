package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.ApplicationPipelineDetailRequest;
import com.orion.ops.entity.request.ApplicationPipelineRequest;
import com.orion.ops.entity.vo.ApplicationPipelineVO;
import com.orion.ops.service.api.ApplicationPipelineService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用流水线 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:17
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-pipeline")
public class ApplicationPipelineController {

    @Resource
    private ApplicationPipelineService applicationPipelineService;

    /**
     * 新增
     */
    @RequestMapping("/add")
    @EventLog(EventType.ADD_PIPELINE)
    public Long addPipeline(@RequestBody ApplicationPipelineRequest request) {
        this.validParams(request);
        return applicationPipelineService.addPipeline(request);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @EventLog(EventType.UPDATE_PIPELINE)
    public Integer updatePipeline(@RequestBody ApplicationPipelineRequest request) {
        Valid.notNull(request.getId());
        this.validParams(request);
        return applicationPipelineService.updatePipeline(request);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<ApplicationPipelineVO> listPipeline(@RequestBody ApplicationPipelineRequest request) {
        Valid.notNull(request.getProfileId());
        return applicationPipelineService.listPipeline(request);
    }

    /**
     * 详情
     */
    @RequestMapping("/get")
    public ApplicationPipelineVO getPipeline(@RequestBody ApplicationPipelineRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineService.getPipeline(id);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @EventLog(EventType.DELETE_PIPELINE)
    public Integer deletePipeline(@RequestBody ApplicationPipelineRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationPipelineService.deletePipeline(idList);
    }

    // 同步到其他环境

    /**
     * 检查参数
     *
     * @param request request
     */
    private void validParams(@RequestBody ApplicationPipelineRequest request) {
        Valid.notBlank(request.getName());
        Valid.notNull(request.getProfileId());
        List<ApplicationPipelineDetailRequest> details = Valid.notEmpty(request.getDetails());
        for (ApplicationPipelineDetailRequest detail : details) {
            Valid.notNull(detail.getAppId());
            Valid.notNull(StageType.of(detail.getStageType()));
        }
    }

}
