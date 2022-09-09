package com.orion.ops.handler.exporter;

import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.ExportType;
import com.orion.ops.dao.ApplicationInfoDAO;
import com.orion.ops.dao.ApplicationRepositoryDAO;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import com.orion.ops.entity.domain.ApplicationRepositoryDO;
import com.orion.ops.entity.exporter.ApplicationExportDTO;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.spring.SpringHolder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 应用信息 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:48
 */
public class ApplicationDataExporter extends AbstractDataExporter<ApplicationExportDTO> {

    private static final ApplicationInfoDAO applicationInfoDAO = SpringHolder.getBean(ApplicationInfoDAO.class);

    private static final ApplicationRepositoryDAO applicationRepositoryDAO = SpringHolder.getBean(ApplicationRepositoryDAO.class);

    public ApplicationDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.APPLICATION, request, response);
    }

    @Override
    protected List<ApplicationExportDTO> queryData() {
        // 查询数据
        List<ApplicationInfoDO> appList = applicationInfoDAO.selectList(null);
        List<ApplicationExportDTO> exportList = Converts.toList(appList, ApplicationExportDTO.class);
        // 仓库名称
        List<Long> repoIdList = appList.stream()
                .map(ApplicationInfoDO::getRepoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!repoIdList.isEmpty()) {
            List<ApplicationRepositoryDO> repoNameList = applicationRepositoryDAO.selectNameByIdList(repoIdList);
            // 设置仓库名称
            for (ApplicationExportDTO export : exportList) {
                Long repoId = export.getRepoId();
                if (repoId == null) {
                    continue;
                }
                repoNameList.stream()
                        .filter(s -> s.getId().equals(repoId))
                        .findFirst()
                        .ifPresent(s -> export.setRepoName(s.getRepoName()));
            }
        }
        return exportList;
    }

}
