package com.orion.ops.handler.alarm.push;

import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.math.Numbers;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.machine.MachineAlarmType;
import com.orion.ops.constant.webhook.WebhookType;
import com.orion.ops.dao.WebhookConfigDAO;
import com.orion.ops.entity.domain.UserInfoDO;
import com.orion.ops.entity.domain.WebhookConfigDO;
import com.orion.ops.handler.alarm.MachineAlarmContext;
import com.orion.ops.handler.webhook.DingRobotPusher;
import com.orion.ops.utils.ResourceLoader;
import com.orion.spring.SpringHolder;

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
