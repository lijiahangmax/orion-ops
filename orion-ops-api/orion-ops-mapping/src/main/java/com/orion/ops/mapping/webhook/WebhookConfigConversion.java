package com.orion.ops.mapping.webhook;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.WebhookConfigDO;
import com.orion.ops.entity.vo.webhook.WebhookConfigVO;

/**
 * webhook 配置 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/23 18:05
 */
public class WebhookConfigConversion {

    static {
        TypeStore.STORE.register(WebhookConfigDO.class, WebhookConfigVO.class, p -> {
            WebhookConfigVO vo = new WebhookConfigVO();
            vo.setId(p.getId());
            vo.setName(p.getWebhookName());
            vo.setUrl(p.getWebhookUrl());
            vo.setType(p.getWebhookType());
            vo.setConfig(p.getWebhookConfig());
            return vo;
        });
    }

}
