package com.orion.ops.handler.exporter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.ExportType;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.dao.WebhookConfigDAO;
import com.orion.ops.entity.domain.WebhookConfigDO;
import com.orion.ops.entity.exporter.WebhookExportDTO;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.spring.SpringHolder;

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
