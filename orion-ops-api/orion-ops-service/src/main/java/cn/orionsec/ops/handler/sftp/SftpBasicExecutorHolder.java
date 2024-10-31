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
package cn.orionsec.ops.handler.sftp;

import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.kit.net.host.SessionStore;
import cn.orionsec.kit.net.host.sftp.SftpExecutor;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.service.api.MachineEnvService;
import cn.orionsec.ops.service.api.MachineInfoService;
import cn.orionsec.ops.service.api.SftpService;
import cn.orionsec.ops.utils.EventParamsHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * sftp基本操作执行holder
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/9 22:30
 */
@Slf4j
@Component
public class SftpBasicExecutorHolder {

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private SftpService sftpService;

    /**
     * 基本操作的executor 不包含(upload, download)
     * machineId: executor
     */
    private final Map<Long, SftpExecutor> basicExecutorHolder = Maps.newCurrentHashMap();

    /**
     * 基本操作executor 最后使用时间
     */
    private final Map<Long, Long> executorUsing = Maps.newCurrentHashMap();

    /**
     * 获取 sftp 基本操作 executor
     *
     * @param sessionToken sessionToken
     * @return SftpExecutor
     */
    public SftpExecutor getBasicExecutor(String sessionToken) {
        // 获取executor
        Long machineId = sftpService.getMachineId(sessionToken);
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, machineId);
        return this.getBasicExecutor(machineId);
    }


    /**
     * 获取 sftp 基本操作 executor
     *
     * @param machineId machineId
     * @return SftpExecutor
     */
    public SftpExecutor getBasicExecutor(Long machineId) {
        return this.getBasicExecutor(machineId, null);
    }

    /**
     * 获取 sftp 基本操作 executor
     *
     * @param machineId machineId
     * @param machine   machine
     * @return SftpExecutor
     */
    public SftpExecutor getBasicExecutor(Long machineId, MachineInfoDO machine) {
        SftpExecutor executor = basicExecutorHolder.get(machineId);
        if (executor != null) {
            if (!executor.isConnected()) {
                try {
                    executor.connect();
                } catch (Exception e) {
                    // 无法连接则重新创建实例
                    executor = null;
                }
            }
        }
        // 如果没有重新建立连接
        if (executor == null) {
            if (machine == null) {
                machine = machineInfoService.selectById(machineId);
            }
            // 获取charset
            String charset = machineEnvService.getSftpCharset(machineId);
            // 打开sftp连接
            SessionStore sessionStore = machineInfoService.openSessionStore(machine);
            executor = sessionStore.getSftpExecutor(charset);
            executor.connect();
            basicExecutorHolder.put(machineId, executor);
        }
        executorUsing.put(machineId, System.currentTimeMillis());
        return executor;
    }

    /**
     * 无效化一段时间(1分钟)未使用的执行器
     */
    @Scheduled(cron = "0 */1 * * * ?")
    private void invalidationUnusedExecutor() {
        long curr = System.currentTimeMillis();
        // 查询需要淘汰的executor的key
        List<Long> expireKeys = basicExecutorHolder.keySet().stream()
                .filter(key -> curr > executorUsing.get(key) + Const.MS_S_60 * 5)
                .collect(Collectors.toList());
        // 移除
        expireKeys.forEach(key -> {
            SftpExecutor sftpExecutor = basicExecutorHolder.get(key);
            if (sftpExecutor == null) {
                return;
            }
            log.info("淘汰sftp执行器: {}", key);
            basicExecutorHolder.remove(key);
            sftpExecutor.disconnect();
        });
    }

}
