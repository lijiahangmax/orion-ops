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

import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.kit.lang.utils.math.Numbers;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.machine.MachineAlarmType;
import cn.orionsec.ops.constant.message.MessageType;
import cn.orionsec.ops.handler.alarm.MachineAlarmContext;
import cn.orionsec.ops.service.api.WebSideMessageService;

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
