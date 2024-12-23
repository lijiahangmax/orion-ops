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

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.KeyConst;
import cn.orionsec.ops.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 清空缓存key
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/6 11:20
 */
@Component
@Order(1200)
@Slf4j
public class CacheKeyCleanRunner implements CommandLineRunner {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) {
        log.info("重启清除缓存-开始");
        List<String> scanKeys = Lists.of(
                // terminal访问token
                Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, "*"),
                // terminal监视token
                Strings.format(KeyConst.TERMINAL_WATCHER_TOKEN, "*"),
                // 文件tail访问token
                Strings.format(KeyConst.FILE_TAIL_ACCESS_TOKEN, "*"),
                // 文件下载token
                Strings.format(KeyConst.FILE_DOWNLOAD_TOKEN, "*"),
                // sftp会话token
                Strings.format(KeyConst.SFTP_SESSION_TOKEN, "*"),
                // sftp上传请求token
                Strings.format(KeyConst.SFTP_UPLOAD_ACCESS_TOKEN, "*"),
                // 首页统计key
                Strings.format(KeyConst.HOME_STATISTICS_COUNT_KEY, "*"),
                // 调度统计key
                Strings.format(KeyConst.SCHEDULER_TASK_STATISTICS_KEY, "*"),
                // 环境缓存key
                KeyConst.DATA_PROFILE_KEY,
                // 数据导入缓存key
                Strings.format(KeyConst.DATA_IMPORT_TOKEN, "*", "*"),
                // 机器分组数据key
                KeyConst.MACHINE_GROUP_DATA_KEY,
                // 机器分组关联key
                KeyConst.MACHINE_GROUP_REL_KEY
        );
        // 查询删除缓存key
        scanKeys.stream()
                .map(key -> RedisUtils.scanKeys(redisTemplate, key, Const.N_10000))
                .filter(Lists::isNotEmpty)
                .peek(keys -> keys.forEach(key -> log.info("重启清除缓存-处理 key: {}", key)))
                .forEach(keys -> redisTemplate.delete(keys));
        log.info("重启清除缓存-结束");
    }

}
