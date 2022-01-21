package com.orion.ops.runner;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.utils.RedisUtils;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
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
@Order(120)
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
                // terminal绑定token
                Strings.format(KeyConst.TERMINAL_BIND_TOKEN, "*"),
                // 文件tail访问token
                Strings.format(KeyConst.FILE_TAIL_ACCESS_TOKEN, "*"),
                // 文件tail绑定token
                Strings.format(KeyConst.FILE_TAIL_BIND_TOKEN, "*"),
                // 文件下载token
                Strings.format(KeyConst.FILE_DOWNLOAD_TOKEN, "*"),
                // sftp会话token
                Strings.format(KeyConst.SFTP_SESSION_TOKEN, "*"),
                // sftp上传请求token
                Strings.format(KeyConst.SFTP_UPLOAD_ACCESS_TOKEN, "*"),
                // 统计数量key
                KeyConst.STATISTICS_COUNT_KEY,
                // 环境缓存key
                KeyConst.DATA_PROFILE_KEY
        );
        // 查询删除缓存key
        scanKeys.stream().map(key -> RedisUtils.scanKeys(redisTemplate, key, Const.N_10000))
                .filter(Lists::isNotEmpty)
                .peek(keys -> keys.forEach(key -> log.info("重启清除缓存-处理 key: {}", key)))
                .forEach(keys -> redisTemplate.delete(keys));
        log.info("重启清除缓存-结束");
    }

}
