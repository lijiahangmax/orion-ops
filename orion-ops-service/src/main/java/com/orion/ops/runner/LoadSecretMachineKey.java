package com.orion.ops.runner;

import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.remote.channel.SessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 加载机群key
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/5 14:09
 */
@Component
@Order(120)
@Slf4j
public class LoadSecretMachineKey implements CommandLineRunner {

    @Resource
    private MachineSecretKeyDAO machineSecretKeyDAO;

    @Override
    public void run(String... args) {
        log.info("集群登陆秘钥加载-开始");
        List<MachineSecretKeyDO> keys = machineSecretKeyDAO.selectList(null);
        for (MachineSecretKeyDO key : keys) {
            String secretKeyPath = key.getSecretKeyPath();
            String password = key.getPassword();
            log.info("加载ssh私钥 {} {}", key.getKeyName(), secretKeyPath);
            if (password != null) {
                SessionHolder.addIdentity(secretKeyPath, password);
            } else {
                SessionHolder.addIdentity(secretKeyPath);
            }
        }
        log.info("集群登陆秘钥加载-结束");
    }

}
