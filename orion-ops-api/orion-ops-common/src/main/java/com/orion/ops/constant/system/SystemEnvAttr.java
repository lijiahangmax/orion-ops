package com.orion.ops.constant.system;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统环境变量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 16:58
 */
@Getter
public enum SystemEnvAttr {

    /**
     * 秘钥存放目录
     */
    KEY_PATH("秘钥存放目录", false),

    /**
     * 图片存放目录
     */
    PIC_PATH("图片存放目录", false),

    /**
     * 交换分区目录
     */
    SWAP_PATH("交换分区目录", false),

    /**
     * 录屏存放目录
     */
    SCREEN_PATH("录屏存放目录", false),

    /**
     * 日志存放目录
     */
    LOG_PATH("日志存放目录", false),

    /**
     * 临时文件目录
     */
    TEMP_PATH("临时文件目录", false),

    /**
     * 应用版本仓库目录
     */
    REPO_PATH("应用版本仓库目录", false),

    /**
     * 构建产物目录
     */
    DIST_PATH("构建产物目录", false),

    /**
     * 机器监控插件绝对路径
     */
    MACHINE_MONITOR_AGENT_PATH("机器监控插件绝对路径", false),

    /**
     * 日志文件上传目录
     */
    TAIL_FILE_UPLOAD_PATH("日志文件上传目录", false),

    /**
     * 日志文件追踪模式 tracker或tail 默认tracker
     */
    TAIL_MODE("日志文件追踪模式 (tracker/tail)", false),

    /**
     * 文件追踪器等待时间 (ms)
     */
    TRACKER_DELAY_TIME("文件追踪器等待时间 (ms)", false),

    /**
     * ip 白名单
     */
    WHITE_IP_LIST("ip 白名单", true),

    /**
     * ip 黑名单
     */
    BLACK_IP_LIST("ip 黑名单", true),

    /**
     * 是否启用 IP 过滤
     */
    ENABLE_IP_FILTER("是否启用IP过滤", true),

    /**
     * 是否启用 IP 白名单
     */
    ENABLE_WHITE_IP_LIST("是否启用IP白名单", true),

    /**
     * 文件清理阈值 (天)
     */
    FILE_CLEAN_THRESHOLD("文件清理阈值 (天)", true),

    /**
     * 是否启用自动清理
     */
    ENABLE_AUTO_CLEAN_FILE("是否启用自动清理", true),

    /**
     * 是否允许多端登陆
     */
    ALLOW_MULTIPLE_LOGIN("允许多端登陆", true),

    /**
     * 是否启用登陆失败锁定
     */
    LOGIN_FAILURE_LOCK("是否启用登陆失败锁定", true),

    /**
     * 是否启用登陆IP绑定
     */
    LOGIN_IP_BIND("是否启用登陆IP绑定", true),

    /**
     * 是否启用凭证自动续签
     */
    LOGIN_TOKEN_AUTO_RENEW("是否启用凭证自动续签", true),

    /**
     * 登陆凭证有效期 (时)
     */
    LOGIN_TOKEN_EXPIRE("登陆凭证有效期 (时)", true),

    /**
     * 登陆失败锁定阈值 (次)
     */
    LOGIN_FAILURE_LOCK_THRESHOLD("登陆失败锁定阈值", true),

    /**
     * 登陆自动续签阈值 (时)
     */
    LOGIN_TOKEN_AUTO_RENEW_THRESHOLD("登陆自动续签阈值 (时)", true),

    /**
     * 自动恢复启用的调度任务
     */
    RESUME_ENABLE_SCHEDULER_TASK("自动恢复启用的调度任务", true),

    /**
     * SFTP 上传文件最大阈值 (MB)
     */
    SFTP_UPLOAD_THRESHOLD("sftp 上传文件最大阈值 (MB)", true),

    /**
     * 统计缓存有效时间 (分)
     */
    STATISTICS_CACHE_EXPIRE("统计缓存有效时间 (分)", true),

    ;

    /**
     * key
     */
    private final String key;

    /**
     * 描述
     */
    private final String description;

    private final boolean systemEnv;

    @Setter
    private String value;

    SystemEnvAttr(String description, boolean systemEnv) {
        this.description = description;
        this.systemEnv = systemEnv;
        this.key = this.name().toLowerCase();
    }

    public static List<String> getKeys() {
        return Arrays.stream(values())
                .map(SystemEnvAttr::getKey)
                .collect(Collectors.toList());
    }

    public static SystemEnvAttr of(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.key.equals(key))
                .findFirst()
                .orElse(null);
    }

}
