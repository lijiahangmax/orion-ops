package com.orion.ops.runner;

import com.orion.ops.constant.EnableType;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.interceptor.IpFilterInterceptor;
import com.orion.ops.service.api.SystemEnvService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 加载系统过滤器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 18:05
 */
@Component
@Order(1300)
@Slf4j
public class LoadIpFilterRunner implements CommandLineRunner {

    @Resource
    private SystemEnvService systemEnvService;

    @Resource
    private IpFilterInterceptor ipFilterInterceptor;

    @Override
    public void run(String... args) {
        log.info("加载IP黑白名单-开始");
        this.load();
        log.info("加载IP黑白名单-结束");
    }

    /**
     * 加载
     */
    private void load() {
        // 启用状态
        String enableIpFilter = systemEnvService.getEnvValue(SystemEnvAttr.ENABLE_IP_FILTER.getKey());
        if (!EnableType.of(enableIpFilter).getValue()) {
            log.info("加载IP黑白名单-未启用");
            return;
        }
        // 规则类型
        String enableWhiteIpList = systemEnvService.getEnvValue(SystemEnvAttr.ENABLE_WHITE_IP_LIST.getKey());
        boolean enableWhite = EnableType.of(enableWhiteIpList).getValue();
        String loadFilterKey;
        if (enableWhite) {
            loadFilterKey = SystemEnvAttr.WHITE_IP_LIST.getKey();
            log.info("加载IP黑白名单-加载白名单");
        } else {
            loadFilterKey = SystemEnvAttr.BLACK_IP_LIST.getKey();
            log.info("加载IP黑白名单-加载黑名单");
        }
        String filter = systemEnvService.getEnvValue(loadFilterKey);
        log.info("加载IP黑白名单-过滤规则:\n{}", filter);
        // 加载
        ipFilterInterceptor.set(true, enableWhite, filter);
    }

}
