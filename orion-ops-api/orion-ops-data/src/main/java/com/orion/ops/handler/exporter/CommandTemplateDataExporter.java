package com.orion.ops.handler.exporter;

import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.ExportType;
import com.orion.ops.dao.CommandTemplateDAO;
import com.orion.ops.entity.domain.CommandTemplateDO;
import com.orion.ops.entity.exporter.CommandTemplateExportDTO;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.spring.SpringHolder;

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
