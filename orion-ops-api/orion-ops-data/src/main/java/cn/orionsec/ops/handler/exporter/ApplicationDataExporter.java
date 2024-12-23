/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
import cn.orionsec.ops.dao.ApplicationInfoDAO;
import cn.orionsec.ops.dao.ApplicationRepositoryDAO;
import cn.orionsec.ops.entity.domain.ApplicationInfoDO;
import cn.orionsec.ops.entity.domain.ApplicationRepositoryDO;
import cn.orionsec.ops.entity.exporter.ApplicationExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;

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
