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
import cn.orionsec.ops.dao.ApplicationProfileDAO;
import cn.orionsec.ops.entity.domain.ApplicationProfileDO;
import cn.orionsec.ops.entity.exporter.ApplicationProfileExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;

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
