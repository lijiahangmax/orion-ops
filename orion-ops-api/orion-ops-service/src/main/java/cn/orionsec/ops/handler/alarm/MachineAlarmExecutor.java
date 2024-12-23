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
package cn.orionsec.ops.handler.alarm;

import cn.orionsec.kit.lang.able.Executable;
import cn.orionsec.kit.lang.utils.Threads;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.alarm.AlarmGroupNotifyType;
import cn.orionsec.ops.dao.UserInfoDAO;
import cn.orionsec.ops.entity.domain.AlarmGroupNotifyDO;
import cn.orionsec.ops.entity.domain.AlarmGroupUserDO;
import cn.orionsec.ops.entity.domain.MachineAlarmGroupDO;
import cn.orionsec.ops.entity.domain.UserInfoDO;
import cn.orionsec.ops.handler.alarm.push.AlarmWebSideMessagePusher;
import cn.orionsec.ops.handler.alarm.push.AlarmWebhookPusher;
import cn.orionsec.ops.service.api.AlarmGroupNotifyService;
import cn.orionsec.ops.service.api.AlarmGroupUserService;
import cn.orionsec.ops.service.api.MachineAlarmGroupService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 机器报警执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 18:30
 */
@Slf4j
public class MachineAlarmExecutor implements Runnable, Executable {

    private static final MachineAlarmGroupService machineAlarmGroupService = SpringHolder.getBean(MachineAlarmGroupService.class);

    private static final AlarmGroupUserService alarmGroupUserService = SpringHolder.getBean(AlarmGroupUserService.class);

    private static final AlarmGroupNotifyService alarmGroupNotifyService = SpringHolder.getBean(AlarmGroupNotifyService.class);

    private static final UserInfoDAO userInfoDAO = SpringHolder.getBean(UserInfoDAO.class);

    private final MachineAlarmContext context;

    public MachineAlarmExecutor(MachineAlarmContext context) {
        this.context = context;
    }

    @Override
    public void exec() {
        Threads.start(this, SchedulerPools.MACHINE_ALARM_SCHEDULER);
    }

    @Override
    public void run() {
        log.info("机器触发报警推送 context: {}", JSON.toJSONString(context));
        // 查询报警组
        List<Long> alarmGroupIdList = machineAlarmGroupService.selectByMachineId(context.getMachineId())
                .stream()
                .map(MachineAlarmGroupDO::getGroupId)
                .collect(Collectors.toList());
        log.info("机器触发报警推送 groupId: {}", alarmGroupIdList);
        if (alarmGroupIdList.isEmpty()) {
            return;
        }
        // 查询报警组员
        List<Long> alarmUserIdList = alarmGroupUserService.selectByGroupIdList(alarmGroupIdList)
                .stream()
                .map(AlarmGroupUserDO::getUserId)
                .distinct()
                .collect(Collectors.toList());
        log.info("机器触发报警推送 userId: {}", alarmUserIdList);
        if (alarmGroupIdList.isEmpty()) {
            return;
        }
        // 查询用户信息
        Map<Long, UserInfoDO> userMapping = userInfoDAO.selectBatchIds(alarmUserIdList)
                .stream()
                .collect(Collectors.toMap(UserInfoDO::getId, Function.identity()));
        context.setUserMapping(userMapping);
        // 通知站内信
        this.doAlarmPush(alarmGroupIdList);
    }

    /**
     * 执行报警推送
     *
     * @param alarmGroupIdList 报警组id
     */
    private void doAlarmPush(List<Long> alarmGroupIdList) {
        // 通知站内信
        new AlarmWebSideMessagePusher(context).push();
        // 通知报警组
        List<AlarmGroupNotifyDO> alarmNotifyList = alarmGroupNotifyService.selectByGroupIdList(alarmGroupIdList);
        for (AlarmGroupNotifyDO alarmNotify : alarmNotifyList) {
            Integer notifyType = alarmNotify.getNotifyType();
            if (AlarmGroupNotifyType.WEBHOOK.getType().equals(notifyType)) {
                // 通知 webhook
                try {
                    new AlarmWebhookPusher(alarmNotify.getNotifyId(), context).push();
                } catch (Exception e) {
                    log.error("机器报警 webhook 推送失败 id: {}", alarmNotify.getNotifyId(), e);
                }
            }
        }
    }

}
