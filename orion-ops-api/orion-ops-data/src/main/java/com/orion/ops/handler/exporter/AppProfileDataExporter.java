package com.orion.ops.handler.exporter;

import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.ExportType;
import com.orion.ops.dao.ApplicationProfileDAO;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.ops.entity.exporter.ApplicationProfileExportDTO;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.spring.SpringHolder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 应用环境 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:46
 */
public class AppProfileDataExporter extends AbstractDataExporter<ApplicationProfileExportDTO> {

    private static final ApplicationProfileDAO applicationProfileDAO = SpringHolder.getBean(ApplicationProfileDAO.class);

    public AppProfileDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.APP_PROFILE, request, response);
    }

    @Override
    protected List<ApplicationProfileExportDTO> queryData() {
        // 查询数据
        List<ApplicationProfileDO> profileList = applicationProfileDAO.selectList(null);
        return Converts.toList(profileList, ApplicationProfileExportDTO.class);
    }

}
