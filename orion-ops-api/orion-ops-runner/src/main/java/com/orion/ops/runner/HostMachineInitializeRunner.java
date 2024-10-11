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
package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.Systems;
import com.orion.lang.utils.net.IPs;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.machine.MachineAuthType;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.utils.ValueMix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 宿主机初始化
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/3/29 18:54
 */
@Component
@Order(1600)
@Slf4j
public class HostMachineInitializeRunner implements CommandLineRunner {

    private static final String DEFAULT_PASSWORD = "orionadmin";

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private MachineEnvService machineEnvService;

    @Override
    public void run(String... args) {
        log.info("初始化宿主机配置-开始");
        this.initMachine();
        log.info("初始化宿主机配置-结束");
    }

    /**
     * 初始化宿主机
     */
    private void initMachine() {
        LambdaQueryWrapper<MachineInfoDO> wrapper = new LambdaQueryWrapper<MachineInfoDO>()
                .eq(MachineInfoDO::getId, Const.HOST_MACHINE_ID);
        MachineInfoDO machineInfo = machineInfoDAO.selectOne(wrapper);
        if (machineInfo == null) {
            // 插入机器
            MachineInfoDO insert = new MachineInfoDO();
            insert.setMachineName(Systems.HOST_NAME);
            insert.setMachineTag(Const.HOST_MACHINE_TAG);
            insert.setMachineHost(IPs.IP);
            insert.setSshPort(22);
            insert.setDescription("宿主机");
            insert.setUsername(Systems.USER_NAME);
            insert.setPassword(ValueMix.encrypt(DEFAULT_PASSWORD));
            insert.setAuthType(MachineAuthType.PASSWORD.getType());
            insert.setMachineStatus(Const.ENABLE);
            machineInfoDAO.insert(insert);
            machineInfoDAO.setId(insert.getId(), Const.HOST_MACHINE_ID);
            log.info("宿主机已初始化");
            // 初始化环境变量
            machineEnvService.initEnv(Const.HOST_MACHINE_ID);
        }
    }

}
