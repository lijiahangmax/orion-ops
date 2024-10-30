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
package cn.orionsec.ops.runner;

import cn.orionsec.ops.constant.common.EnableType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.service.api.SystemService;
import cn.orionsec.ops.service.api.UserService;
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
