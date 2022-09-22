package com.orion.ops.handler.alarm.push;

import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.math.Numbers;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.machine.MachineAlarmType;
import com.orion.ops.constant.message.MessageType;
import com.orion.ops.handler.alarm.MachineAlarmContext;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.spring.SpringHolder;

import java.util.Map;

/**
 * 机器报警站内信推送
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 18:41
 */
public class AlarmWebSideMessagePusher implements IAlarmPusher {

    private static final WebSideMessageService webSideMessageService = SpringHolder.getBean(WebSideMessageService.class);

    private final MachineAlarmContext context;

    public AlarmWebSideMessagePusher(MachineAlarmContext context) {
        this.context = context;
    }

    @Override
    public void push() {
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.NAME, context.getMachineName());
        params.put(EventKeys.HOST, context.getMachineHost());
        params.put(EventKeys.TYPE, MachineAlarmType.of(context.getAlarmType()).getLabel());
        params.put(EventKeys.VALUE, Numbers.setScale(context.getAlarmValue(), 2));
        params.put(EventKeys.TIME, Dates.format(context.getAlarmTime()));
        context.getUserMapping().forEach((k, v) -> {
            webSideMessageService.addMessage(MessageType.MACHINE_ALARM, context.getMachineId(), k, v.getUsername(), params);
        });
    }

}
