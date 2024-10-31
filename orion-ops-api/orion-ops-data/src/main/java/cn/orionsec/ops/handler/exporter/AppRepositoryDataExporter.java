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
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.ExportType;
import cn.orionsec.ops.dao.ApplicationRepositoryDAO;
import cn.orionsec.ops.entity.domain.ApplicationRepositoryDO;
import cn.orionsec.ops.entity.exporter.ApplicationRepositoryExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 应用仓库 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:50
 */
public class AppRepositoryDataExporter extends AbstractDataExporter<ApplicationRepositoryExportDTO> {

    private static final ApplicationRepositoryDAO applicationRepositoryDAO = SpringHolder.getBean(ApplicationRepositoryDAO.class);

    public AppRepositoryDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.APP_REPOSITORY, request, response);
    }

    @Override
    protected List<ApplicationRepositoryExportDTO> queryData() {
        // 查询数据
        List<ApplicationRepositoryDO> repoList = applicationRepositoryDAO.selectList(null);
        List<ApplicationRepositoryExportDTO> exportList = Converts.toList(repoList, ApplicationRepositoryExportDTO.class);
        if (!Const.ENABLE.equals(request.getExportPassword())) {
            exportList.forEach(s -> s.setEncryptAuthValue(null));
        }
        return exportList;
    }

}
