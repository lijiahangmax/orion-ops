package com.orion.ops.runner;

import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.utils.ValueMix;
import com.orion.remote.channel.SessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
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
            File secretKey = new File(secretKeyPath);
            if (!secretKey.exists() || !secretKey.isFile()) {
                log.info("加载ssh私钥失败 未找到文件 {} {}", key.getKeyName(), secretKeyPath);
                continue;
            }
            String password = key.getPassword();
            log.info("加载ssh私钥 {} {}", key.getKeyName(), secretKeyPath);
            if (password != null) {
                String decrypt = ValueMix.decrypt(password);
                SessionHolder.addIdentity(secretKeyPath, decrypt);
            } else {
                SessionHolder.addIdentity(secretKeyPath);
            }
        }
        log.info("集群登陆秘钥加载-结束");
    }

}
