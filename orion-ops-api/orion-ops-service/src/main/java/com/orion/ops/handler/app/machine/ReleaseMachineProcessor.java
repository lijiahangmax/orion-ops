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
package com.orion.ops.handler.app.machine;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.io.Streams;
import com.orion.lang.utils.time.Dates;
import com.orion.net.remote.channel.SessionStore;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.app.ActionStatus;
import com.orion.ops.constant.app.StageType;
import com.orion.ops.constant.common.StainCode;
import com.orion.ops.dao.ApplicationMachineDAO;
import com.orion.ops.dao.ApplicationReleaseMachineDAO;
import com.orion.ops.entity.domain.*;
import com.orion.ops.handler.app.action.IActionHandler;
import com.orion.ops.handler.app.action.MachineActionStore;
import com.orion.ops.service.api.ApplicationActionLogService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.utils.Utils;
import com.orion.spring.SpringHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * 发布机器执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/12 16:15
 */
@Slf4j
public class ReleaseMachineProcessor extends AbstractMachineProcessor {

    protected static ApplicationReleaseMachineDAO applicationReleaseMachineDAO = SpringHolder.getBean(ApplicationReleaseMachineDAO.class);

    protected static ApplicationMachineDAO applicationMachineDAO = SpringHolder.getBean(ApplicationMachineDAO.class);

    protected static ApplicationActionLogService applicationActionLogService = SpringHolder.getBean(ApplicationActionLogService.class);

    protected static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private final ApplicationReleaseDO release;

    private final ApplicationReleaseMachineDO releaseMachine;

    private final MachineActionStore store;

    @Getter
    private volatile ActionStatus status;

    public ReleaseMachineProcessor(ApplicationReleaseDO release, ApplicationReleaseMachineDO releaseMachine) {
        super(releaseMachine.getId());
        this.release = release;
        this.releaseMachine = releaseMachine;
        this.status = ActionStatus.of(releaseMachine.getRunStatus());
        this.store = new MachineActionStore();
        store.setRelId(releaseMachine.getId());
        store.setMachineId(releaseMachine.getMachineId());
        store.setBundlePath(release.getBundlePath());
        store.setTransferPath(release.getTransferPath());
        store.setTransferMode(release.getTransferMode());
    }

    @Override
    public void run() {
        log.info("应用发布任务执行开始 releaseId: {}", id);
        // 初始化数据
        this.initData();
        // 执行
        super.run();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 查询机器发布操作
        List<ApplicationActionLogDO> actions = applicationActionLogService.selectActionByRelId(id, StageType.RELEASE);
        actions.forEach(s -> store.getActions().put(s.getId(), s));
        log.info("应用发布器-获取数据-action releaseId: {}, actions: {}", id, JSON.toJSONString(actions));
        // 创建handler
        this.handlerList = IActionHandler.createHandler(actions, store);
    }

    @Override
    public void skip() {
        if (ActionStatus.WAIT.equals(status)) {
            // 只能跳过等待中的任务
            this.updateStatus(MachineProcessorStatus.SKIPPED);
        }
    }

    @Override
    protected boolean checkCanRunnable() {
        return ActionStatus.WAIT.equals(status);
    }

    @Override
    protected void openLogger() {
        super.openLogger();
        store.setSuperLogStream(this.logStream);
    }

    @Override
    protected String getLogPath() {
        return releaseMachine.getLogPath();
    }

    @Override
    protected void successCallback() {
        // 完成回调
        super.successCallback();
        // 更新应用机器发布版本
        this.updateAppMachineVersion();
    }

    @Override
    protected void exceptionCallback(boolean isMainError, Exception ex) {
        super.exceptionCallback(isMainError, ex);
        throw Exceptions.runtime(ex);
    }

    @Override
    protected void openMachineSession() {
        // 打开目标机器session
        Long machineId = releaseMachine.getMachineId();
        MachineInfoDO machine = machineInfoService.selectById(machineId);
        SessionStore sessionStore = machineInfoService.openSessionStore(machine);
        store.setSessionStore(sessionStore);
        store.setMachineUsername(machine.getUsername());
        store.setMachineHost(machine.getMachineHost());
    }

    @Override
    protected void updateStatus(MachineProcessorStatus processorStatus) {
        this.status = ActionStatus.valueOf(processorStatus.name());
        Date now = new Date();
        ApplicationReleaseMachineDO update = new ApplicationReleaseMachineDO();
        update.setId(id);
        update.setRunStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (processorStatus) {
            case RUNNABLE:
                this.startTime = now;
                update.setStartTime(now);
                break;
            case FINISH:
            case TERMINATED:
            case FAILURE:
                this.endTime = now;
                update.setEndTime(now);
                break;
            default:
                break;
        }
        applicationReleaseMachineDAO.updateById(update);
    }

    @Override
    protected void appendStartedLog() {
        StringBuilder log = new StringBuilder()
                .append(Utils.getStainKeyWords("# 开始执行主机发布任务", StainCode.GLOSS_GREEN))
                .append(Const.LF);
        log.append("机器名称: ")
                .append(Utils.getStainKeyWords(releaseMachine.getMachineName(), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        log.append("发布主机: ")
                .append(Utils.getStainKeyWords(releaseMachine.getMachineHost(), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        log.append("开始时间: ")
                .append(Utils.getStainKeyWords(Dates.format(startTime), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 更新应用机器版本
     */
    private void updateAppMachineVersion() {
        ApplicationMachineDO update = new ApplicationMachineDO();
        update.setAppId(release.getAppId());
        update.setProfileId(release.getProfileId());
        update.setMachineId(releaseMachine.getMachineId());
        update.setReleaseId(release.getId());
        update.setBuildId(release.getBuildId());
        update.setBuildSeq(release.getBuildSeq());
        applicationMachineDAO.updateAppVersion(update);
    }

    @Override
    public void close() {
        // 释放资源
        super.close();
        // 释放连接
        Streams.close(store.getSessionStore());
    }
}
