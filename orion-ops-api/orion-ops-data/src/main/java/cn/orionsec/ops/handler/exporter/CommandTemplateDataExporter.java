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
package cn.orionsec.ops.handler.exporter;

import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.ExportType;
import cn.orionsec.ops.dao.CommandTemplateDAO;
import cn.orionsec.ops.entity.domain.CommandTemplateDO;
import cn.orionsec.ops.entity.exporter.CommandTemplateExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 命令模板 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:52
 */
public class CommandTemplateDataExporter extends AbstractDataExporter<CommandTemplateExportDTO> {

    private static final CommandTemplateDAO commandTemplateDAO = SpringHolder.getBean(CommandTemplateDAO.class);

    public CommandTemplateDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.COMMAND_TEMPLATE, request, response);
    }

    @Override
    protected List<CommandTemplateExportDTO> queryData() {
        List<CommandTemplateDO> templateList = commandTemplateDAO.selectList(null);
        return Converts.toList(templateList, CommandTemplateExportDTO.class);
    }

}
