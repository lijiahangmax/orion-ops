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
package cn.orionsec.ops.controller;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.constant.webhook.WebhookType;
import cn.orionsec.ops.entity.request.webhook.WebhookConfigRequest;
import cn.orionsec.ops.entity.vo.webhook.WebhookConfigVO;
import cn.orionsec.ops.service.api.WebhookConfigService;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * webhook 配置服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/23 17:57
 */
@Api(tags = "webhook配置")
@RestController
@RestWrapper
@RequestMapping("/orion/api/webhook-config")
public class WebhookConfigController {

    @Resource
    private WebhookConfigService webhookConfigService;

    @PostMapping("/list")
    @ApiOperation(value = "查询列表")
    public DataGrid<WebhookConfigVO> getWebhookList(@RequestBody WebhookConfigRequest request) {
        return webhookConfigService.getWebhookList(request);
    }

    @PostMapping("/get")
    @ApiOperation(value = "获取详情")
    public WebhookConfigVO getWebhookDetail(@RequestBody WebhookConfigRequest request) {
        Long id = Valid.notNull(request.getId());
        return webhookConfigService.getWebhookDetail(id);
    }

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加 webhook")
    @EventLog(EventType.ADD_WEBHOOK)
    public Long addWebhook(@RequestBody WebhookConfigRequest request) {
        this.checkParams(request);
        return webhookConfigService.addWebhook(request);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "更新 webhook")
    @EventLog(EventType.UPDATE_WEBHOOK)
    public Integer updateWebhook(@RequestBody WebhookConfigRequest request) {
        Valid.notNull(request.getId());
        this.checkParams(request);
        return webhookConfigService.updateWebhook(request);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除 webhook")
    @EventLog(EventType.DELETE_WEBHOOK)
    public Integer deleteWebhook(@RequestBody WebhookConfigRequest request) {
        Long id = Valid.notNull(request.getId());
        return webhookConfigService.deleteWebhook(id);
    }

    /**
     * 检查参数
     *
     * @param request request
     */
    private void checkParams(WebhookConfigRequest request) {
        Valid.notNull(WebhookType.of(request.getType()));
        Valid.allNotBlank(request.getName(), request.getUrl());
    }

}
