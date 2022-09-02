package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.constant.webhook.WebhookType;
import com.orion.ops.entity.request.webhook.WebhookConfigRequest;
import com.orion.ops.entity.vo.webhook.WebhookConfigVO;
import com.orion.ops.service.api.WebhookConfigService;
import com.orion.ops.utils.Valid;
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

    @PostMapping("/add")
    @ApiOperation(value = "添加 webhook")
    @EventLog(EventType.ADD_WEBHOOK)
    public Long addWebhook(@RequestBody WebhookConfigRequest request) {
        this.checkParams(request);
        return webhookConfigService.addWebhook(request);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新 webhook")
    @EventLog(EventType.UPDATE_WEBHOOK)
    public Integer updateWebhook(@RequestBody WebhookConfigRequest request) {
        Valid.notNull(request.getId());
        this.checkParams(request);
        return webhookConfigService.updateWebhook(request);
    }

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
