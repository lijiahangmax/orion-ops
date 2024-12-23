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
package cn.orionsec.ops.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置常量
 * <p>
 * 禁止手动赋值
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/14 17:11
 */
@Component
public class PropertiesConst {

    /**
     * 当前版本
     */
    public static String ORION_OPS_VERSION;

    /**
     * 登录 token 请求头
     */
    public static String LOGIN_TOKEN_HEADER;

    /**
     * 加密密钥
     */
    public static String VALUE_MIX_SECRET_KEY;

    /**
     * 机器监控插件最新版本
     */
    public static String MACHINE_MONITOR_LATEST_VERSION;

    @Value("${app.version}")
    private void setVersion(String version) {
        PropertiesConst.ORION_OPS_VERSION = version;
    }

    @Value("${login.token.header}")
    private void setLoginTokenHeader(String loginTokenHeader) {
        PropertiesConst.LOGIN_TOKEN_HEADER = loginTokenHeader;
    }

    @Value("${value.mix.secret.key}")
    private void setValueMixSecretKey(String valueMixSecretKey) {
        PropertiesConst.VALUE_MIX_SECRET_KEY = valueMixSecretKey;
    }

    @Value("${machine.monitor.latest.version}")
    private void setMachineMonitorLatestVersion(String agentVersion) {
        PropertiesConst.MACHINE_MONITOR_LATEST_VERSION = agentVersion;
    }

}
