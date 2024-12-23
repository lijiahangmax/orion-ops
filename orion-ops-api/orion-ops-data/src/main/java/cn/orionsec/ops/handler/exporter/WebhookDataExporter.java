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
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.dao.WebhookConfigDAO;
import cn.orionsec.ops.entity.domain.WebhookConfigDO;
import cn.orionsec.ops.entity.exporter.WebhookExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;
import cn.orionsec.ops.utils.EventParamsHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * webhook 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 10:16
 */
public class WebhookDataExporter extends AbstractDataExporter<WebhookExportDTO> {

    private static final WebhookConfigDAO webhookConfigDAO = SpringHolder.getBean(WebhookConfigDAO.class);

    public WebhookDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.WEBHOOK, request, response);
    }

    @Override
    protected List<WebhookExportDTO> queryData() {
        // 查询数据
        Integer type = request.getType();
        LambdaQueryWrapper<WebhookConfigDO> wrapper = new LambdaQueryWrapper<WebhookConfigDO>()
                .eq(Objects.nonNull(type), WebhookConfigDO::getWebhookType, type);
        List<WebhookConfigDO> list = webhookConfigDAO.selectList(wrapper);
        return Converts.toList(list, WebhookExportDTO.class);
    }

    @Override
    protected void setEventParams() {
        super.setEventParams();
        EventParamsHolder.addParam(EventKeys.TYPE, request.getType());
    }

}
