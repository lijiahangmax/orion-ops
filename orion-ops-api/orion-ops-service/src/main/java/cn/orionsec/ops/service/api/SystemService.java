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
package cn.orionsec.ops.service.api;

import cn.orionsec.ops.constant.system.SystemCleanType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.entity.request.system.ConfigIpListRequest;
import cn.orionsec.ops.entity.vo.system.IpListConfigVO;
import cn.orionsec.ops.entity.vo.system.SystemAnalysisVO;
import cn.orionsec.ops.entity.vo.system.SystemOptionVO;
import cn.orionsec.ops.entity.vo.system.ThreadPoolMetricsVO;

import java.util.List;

/**
 * 系统服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:15
 */
public interface SystemService {

    /**
     * 获取 ip 配置
     *
     * @param ip ip
     * @return ip 信息
     */
    IpListConfigVO getIpInfo(String ip);

    /**
     * 配置 ip 过滤器列表
     *
     * @param request request
     */
    void configIpFilterList(ConfigIpListRequest request);

    /**
     * 清理系统文件
     *
     * @param cleanType 文件类型
     */
    void cleanSystemFile(SystemCleanType cleanType);

    /**
     * 分析磁盘占用空间
     */
    void analysisSystemSpace();

    /**
     * 获取系统分析信息
     *
     * @return 系统分析信息
     */
    SystemAnalysisVO getSystemAnalysis();

    /**
     * 更新系统配置
     *
     * @param env   env
     * @param value value
     */
    void updateSystemOption(SystemEnvAttr env, String value);

    /**
     * 获取系统配置项
     *
     * @return 配置项
     */
    SystemOptionVO getSystemOptions();

    /**
     * 获取线程池指标
     *
     * @return 指标
     */
    List<ThreadPoolMetricsVO> getThreadPoolMetrics();

}
