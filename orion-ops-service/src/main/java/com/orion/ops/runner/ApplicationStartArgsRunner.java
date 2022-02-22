package com.orion.ops.runner;

import com.orion.ops.consts.EnableType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.service.api.SystemService;
import com.orion.ops.service.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 应用启动参数执行
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/20 1:23
 */
@Component
@Order(4000)
@Slf4j
public class ApplicationStartArgsRunner implements CommandLineRunner {

    /**
     * 关闭ip过滤器
     */
    private static final String DISABLE_IP_FILTER = "--disable-ip-filter";

    /**
     * 自动生成默认管理员账号
     */
    private static final String GENERATOR_ADMIN = "--generator-admin";

    /**
     * 重置默认管理员密码
     */
    private static final String RESET_ADMIN = "--reset-admin";

    @Resource
    private SystemService systemService;

    @Resource
    private UserService userService;

    @Override
    public void run(String... args) {
        log.info("应用启动参数: {}", Arrays.toString(args));
        for (String arg : args) {
            switch (arg) {
                case DISABLE_IP_FILTER:
                    // 关闭ip过滤器
                    this.disableIpFilter();
                    break;
                case GENERATOR_ADMIN:
                    // 生成默认管理员账号
                    this.generatorDefaultAdminUser();
                    break;
                case RESET_ADMIN:
                    // 重置默认管理员密码
                    this.resetDefaultAdminUser();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 关闭ip过滤器
     */
    private void disableIpFilter() {
        systemService.updateSystemOption(SystemEnvAttr.ENABLE_IP_FILTER, EnableType.DISABLED.getLabel());
        log.info("启动参数-IP过滤器已关闭");
    }

    /**
     * 生成默认管理员账号
     */
    private void generatorDefaultAdminUser() {
        userService.generatorDefaultAdminUser();
        log.info("启动参数-默认管理员用户已初始化");
    }

    /**
     * 重置默认管理员密码
     */
    private void resetDefaultAdminUser() {
        userService.resetDefaultAdminUserPassword();
        log.info("启动参数-默认管理员密码已重置");
    }

}
