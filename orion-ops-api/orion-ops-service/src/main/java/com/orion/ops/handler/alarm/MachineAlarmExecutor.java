package com.orion.ops.handler.alarm;

import com.alibaba.fastjson.JSON;
import com.orion.lang.able.Executable;
import com.orion.lang.utils.Threads;
import com.orion.ops.constant.SchedulerPools;
import com.orion.ops.constant.alarm.AlarmGroupNotifyType;
import com.orion.ops.dao.UserInfoDAO;
import com.orion.ops.entity.domain.AlarmGroupNotifyDO;
import com.orion.ops.entity.domain.AlarmGroupUserDO;
import com.orion.ops.entity.domain.MachineAlarmGroupDO;
import com.orion.ops.entity.domain.UserInfoDO;
import com.orion.ops.handler.alarm.push.AlarmWebSideMessagePusher;
import com.orion.ops.handler.alarm.push.AlarmWebhookPusher;
import com.orion.ops.service.api.AlarmGroupNotifyService;
import com.orion.ops.service.api.AlarmGroupUserService;
import com.orion.ops.service.api.MachineAlarmGroupService;
import com.orion.spring.SpringHolder;
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
