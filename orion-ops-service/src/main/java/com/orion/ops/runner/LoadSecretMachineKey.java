package com.orion.ops.runner;

import com.orion.ops.service.api.MachineKeyService;
import com.orion.remote.channel.SessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 加载机器key
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
    private MachineKeyService machineKeyService;

    @Override
    public void run(String... args) {
        log.info("机器登陆秘钥加载-开始");
        machineKeyService.mountAllKey();
        log.info("机器登陆秘钥加载-完成");
        for (String loadKey : SessionHolder.getLoadKeys()) {
            log.info("机器登陆秘钥已加载-{}", loadKey);
        }
        log.info("机器登陆秘钥加载-结束");
    }

}
