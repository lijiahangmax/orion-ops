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
package cn.orionsec.ops.handler.alarm.push;

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.kit.lang.utils.math.Numbers;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.machine.MachineAlarmType;
import cn.orionsec.ops.constant.webhook.WebhookType;
import cn.orionsec.ops.dao.WebhookConfigDAO;
import cn.orionsec.ops.entity.domain.UserInfoDO;
import cn.orionsec.ops.entity.domain.WebhookConfigDO;
import cn.orionsec.ops.handler.alarm.MachineAlarmContext;
import cn.orionsec.ops.handler.webhook.DingRobotPusher;
import cn.orionsec.ops.utils.ResourceLoader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 机器报警 webhook 推送
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 18:41
 */
public class AlarmWebhookPusher implements IAlarmPusher {

    private static final String DING_TEMPLATE = "/templates/push/machine-alarm-ding.template";

    private static WebhookConfigDAO webhookConfigDAO = SpringHolder.getBean(WebhookConfigDAO.class);

    private final Long webhookId;

    private final MachineAlarmContext context;

    public AlarmWebhookPusher(Long webhookId, MachineAlarmContext context) {
        this.webhookId = webhookId;
        this.context = context;
    }

    @Override
    public void push() {
        // 查询 webhook
        WebhookConfigDO webhook = webhookConfigDAO.selectById(webhookId);
        if (webhook == null) {
            return;
        }
        // 触发 webhook
        if (WebhookType.DING_ROBOT.getType().equals(webhook.getWebhookType())) {
            // 钉钉机器人
            this.doDingRobotPush(webhook);
        }
    }

    /**
     * 执行钉钉机器人推送
     *
     * @param webhook webhook
     */
    private void doDingRobotPush(WebhookConfigDO webhook) {
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.NAME, context.getMachineName());
        params.put(EventKeys.HOST, context.getMachineHost());
        params.put(EventKeys.VALUE, Numbers.setScale(context.getAlarmValue(), 2));
        params.put(EventKeys.TYPE, MachineAlarmType.of(context.getAlarmType()).getLabel());
        params.put(EventKeys.TIME, Dates.format(context.getAlarmTime()));
        String text = Strings.format(ResourceLoader.get(DING_TEMPLATE, AlarmWebhookPusher.class), params);
        // @ 的用户
        List<String> atMobiles = context.getUserMapping()
                .values()
                .stream()
                .map(UserInfoDO::getContactPhone)
                .collect(Collectors.toList());
        // 推送
        DingRobotPusher.builder()
                .url(webhook.getWebhookUrl())
                .title("机器发生报警")
                .text(text)
                .atMobiles(atMobiles)
                .build()
                .push();
    }

}
