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
package cn.orionsec.ops.service.api;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.entity.request.webhook.WebhookConfigRequest;
import cn.orionsec.ops.entity.vo.webhook.WebhookConfigVO;

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
