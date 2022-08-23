package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.request.webhook.WebhookConfigRequest;
import com.orion.ops.entity.vo.webhook.WebhookConfigVO;

/**
 * webhook 配置服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/23 17:55
 */
public interface WebhookConfigService {

    /**
     * 查询列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<WebhookConfigVO> getWebhookList(WebhookConfigRequest request);

    /**
     * 获取详情
     *
     * @param id id
     * @return row
     */
    WebhookConfigVO getWebhookDetail(Long id);

    /**
     * 添加
     *
     * @param request request
     * @return id
     */
    Long addWebhook(WebhookConfigRequest request);

    /**
     * 更新
     *
     * @param request request
     * @return effect
     */
    Integer updateWebhook(WebhookConfigRequest request);

    /**
     * 通过 id 删除
     *
     * @param id id
     * @return effect
     */
    Integer deleteWebhook(Long id);

}
