package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.CommandTemplateRequest;
import com.orion.ops.entity.vo.CommandTemplateVO;
import com.orion.ops.service.api.CommandTemplateService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 命令模板 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 17:04
 */
@Api(tags = "命令模板")
@RestController
@RestWrapper
@RequestMapping("/orion/api/template")
public class CommandTemplateController {

    @Resource
    private CommandTemplateService commandTemplateService;

    @PostMapping("/add")
    @ApiOperation(value = "新增命令模板")
    @EventLog(EventType.ADD_TEMPLATE)
    public Long add(@RequestBody CommandTemplateRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getValue());
        return commandTemplateService.addTemplate(request);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改命令模板")
    @EventLog(EventType.UPDATE_TEMPLATE)
    public Integer update(@RequestBody CommandTemplateRequest request) {
        Valid.notNull(request.getId());
        Valid.notBlank(request.getValue());
        return commandTemplateService.updateTemplate(request);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取命令模板列表")
    public DataGrid<CommandTemplateVO> list(@RequestBody CommandTemplateRequest request) {
        return commandTemplateService.listTemplate(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取命令模板详情")
    public CommandTemplateVO detail(@RequestBody CommandTemplateRequest request) {
        Long id = Valid.notNull(request.getId());
        return commandTemplateService.templateDetail(id);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除命令模板")
    @EventLog(EventType.DELETE_TEMPLATE)
    public Integer delete(@RequestBody CommandTemplateRequest request) {
        List<Long> idList = Valid.notNull(request.getIdList());
        return commandTemplateService.deleteTemplate(idList);
    }

}
