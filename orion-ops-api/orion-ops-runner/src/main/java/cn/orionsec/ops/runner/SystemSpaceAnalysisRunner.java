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

import cn.orionsec.ops.service.api.SystemService;
import com.orion.lang.utils.Threads;
import com.orion.lang.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统占用磁盘分析任务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/17 15:18
 */
@Slf4j
@Component
@Order(3200)
public class SystemSpaceAnalysisRunner implements CommandLineRunner {

    @Resource
    private SystemService systemService;

    @Override
    public void run(String... args) {
        try {
            log.info("runner-执行占用磁盘空间统计-开始 {}", Dates.current());
            // 不考虑多线程计算
            Threads.start(systemService::analysisSystemSpace);
            log.info("runner-执行占用磁盘空间统计-结束 {}", Dates.current());
        } catch (Exception e) {
            log.error("runner-执行占用磁盘空间统计-失败 {}", Dates.current(), e);
        }
    }

}
