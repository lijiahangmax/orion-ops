package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.request.template.CommandTemplateRequest;
import com.orion.ops.entity.vo.template.CommandTemplateVO;

import java.util.List;

/**
 * 命令模板api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 17:05
 */
public interface CommandTemplateService {

    /**
     * 添加模板
     *
     * @param request request
     * @return id
     */
    Long addTemplate(CommandTemplateRequest request);

    /**
     * 更新模板
     *
     * @param request request
     * @return effect
     */
    Integer updateTemplate(CommandTemplateRequest request);

    /**
     * 模板列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<CommandTemplateVO> listTemplate(CommandTemplateRequest request);

    /**
     * 模板详情
     *
     * @param id id
     * @return vo
     */
    CommandTemplateVO templateDetail(Long id);

    /**
     * 删除模板
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteTemplate(List<Long> idList);

}
