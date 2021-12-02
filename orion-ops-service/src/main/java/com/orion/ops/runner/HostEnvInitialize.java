package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.MachineEnvDAO;
import com.orion.ops.entity.domain.MachineEnvDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 宿主机基础环境初始化
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:16
 */
@Component
@Order(110)
@Slf4j
public class HostEnvInitialize implements CommandLineRunner {

    @Resource
    private MachineEnvDAO machineEnvDAO;

    @Override
    public void run(String... args) {
        log.info("宿主机基础环境初始化-开始");
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, Const.HOST_MACHINE_ID);
        List<MachineEnvDO> envList = machineEnvDAO.selectList(wrapper);
        for (MachineEnvDO env : envList) {
            MachineEnvAttr attr = MachineEnvAttr.of(env.getAttrKey());
            if (attr != null) {
                attr.setValue(env.getAttrValue());
            }
        }
        log.info("宿主机基础环境初始化-结束");
    }

}
