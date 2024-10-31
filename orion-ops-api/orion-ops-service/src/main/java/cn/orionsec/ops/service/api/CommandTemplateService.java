/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.service.api;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.entity.request.template.CommandTemplateRequest;
import cn.orionsec.ops.entity.vo.template.CommandTemplateVO;

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
