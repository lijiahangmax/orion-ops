package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.TemplateType;
import com.orion.ops.entity.request.CommandTemplateRequest;
import com.orion.ops.entity.vo.CommandTemplateVO;
import com.orion.ops.service.api.CommandTemplateService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 命令模板 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 17:04
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/template")
public class CommandTemplateController {

    @Resource
    private CommandTemplateService commandTemplateService;

    /**
     * 新增
     */
    @RequestMapping("/add")
    public Long add(@RequestBody CommandTemplateRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getValue());
        Valid.notNull(TemplateType.of(request.getType()));
        return commandTemplateService.addTemplate(request);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Integer update(@RequestBody CommandTemplateRequest request) {
        Valid.notNull(request.getId());
        Integer type = request.getType();
        if (type != null) {
            Valid.notNull(TemplateType.of(type));
        }
        return commandTemplateService.updateTemplate(request);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<CommandTemplateVO> list(@RequestBody CommandTemplateRequest request) {
        return commandTemplateService.listTemplate(request);
    }

    /**
     * 详情
     */
    @RequestMapping("/detail")
    public CommandTemplateVO detail(@RequestBody CommandTemplateRequest request) {
        Long id = Valid.notNull(request.getId());
        return commandTemplateService.templateDetail(id);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Integer delete(@RequestBody CommandTemplateRequest request) {
        Long id = Valid.notNull(request.getId());
        return commandTemplateService.deleteTemplate(id);
    }

}
