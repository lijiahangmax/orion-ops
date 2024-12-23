/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
import cn.orionsec.ops.interceptor.IpFilterInterceptor;
import cn.orionsec.ops.service.api.SystemEnvService;
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
