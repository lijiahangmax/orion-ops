package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.EnableType;
import com.orion.ops.consts.history.HistoryOperator;
import com.orion.ops.consts.history.HistoryValueType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.consts.tail.FileTailMode;
import com.orion.ops.dao.SystemEnvDAO;
import com.orion.ops.entity.domain.SystemEnvDO;
import com.orion.ops.service.api.HistoryValueService;
import com.orion.ops.utils.PathBuilders;
import com.orion.utils.io.Files1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统环境变量初始化
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:16
 */
@Component
@Order(1500)
@Slf4j
public class SystemEnvInitialize implements CommandLineRunner {

    @Resource
    private SystemEnvDAO systemEnvDAO;

    @Resource
    private HistoryValueService historyValueService;

    @Override
    public void run(String... args) {
        log.info("初始化系统环境初始化-开始");
        this.initEnv();
        log.info("初始化系统环境初始化-结束");
    }

    /**
     * 初始化环境
     */
    private void initEnv() {
        // 查询所有的key
        List<String> keys = SystemEnvAttr.getKeys();
        LambdaQueryWrapper<SystemEnvDO> wrapper = new LambdaQueryWrapper<SystemEnvDO>()
                .in(SystemEnvDO::getAttrKey, keys);
        List<SystemEnvDO> envList = systemEnvDAO.selectList(wrapper);
        // 初始化数据
        for (String key : keys) {
            SystemEnvAttr attr = SystemEnvAttr.of(key);
            SystemEnvDO env = envList.stream()
                    .filter(s -> s.getAttrKey().equals(key))
                    .findFirst()
                    .orElse(null);
            if (env == null) {
                // 插入数据
                String value = this.getAttrValue(attr);
                SystemEnvDO insert = new SystemEnvDO();
                insert.setAttrKey(key);
                insert.setAttrValue(value);
                insert.setDescription(attr.getDescription());
                insert.setSystemEnv(attr.isSystemEnv() ? Const.IS_SYSTEM : Const.NOT_SYSTEM);
                systemEnvDAO.insert(insert);
                log.info("初始化系统变量 {} - {}", key, value);
                // 插入历史值
                Long id = insert.getId();
                historyValueService.addHistory(id, HistoryValueType.SYSTEM_ENV, HistoryOperator.ADD, null, insert.getAttrValue());
                // 设置本地值
                attr.setValue(value);
            } else {
                // 设置本地值
                attr.setValue(env.getAttrValue());
            }
        }
    }

    /**
     * 获取属性值
     *
     * @param attr attr
     * @return value
     */
    private String getAttrValue(SystemEnvAttr attr) {
        switch (attr) {
            case KEY_PATH:
                return createOrionOpsPath(Const.KEYS_PATH);
            case PIC_PATH:
                return createOrionOpsPath(Const.PIC_PATH);
            case SWAP_PATH:
                return createOrionOpsPath(Const.SWAP_PATH);
            case LOG_PATH:
                return createOrionOpsPath(Const.LOG_PATH);
            case TEMP_PATH:
                return createOrionOpsPath(Const.TEMP_PATH);
            case VCS_PATH:
                return createOrionOpsPath(Const.VCS_PATH);
            case DIST_PATH:
                return createOrionOpsPath(Const.DIST_PATH);
            case TAIL_MODE:
                return FileTailMode.TRACKER.getMode();
            case ENABLE_IP_FILTER:
            case ENABLE_WHITE_IP_LIST:
            case ENABLE_AUTO_CLEAN_FILE:
            case ALLOW_MULTIPLE_LOGIN:
            case RESUME_ENABLE_SCHEDULER_TASK:
                return EnableType.DISABLED.getLabel();
            case LOGIN_FAILURE_LOCK:
            case LOGIN_IP_BIND:
            case LOGIN_TOKEN_AUTO_RENEW:
                return EnableType.ENABLED.getLabel();
            case LOGIN_TOKEN_EXPIRE:
                return Const.DEFAULT_LOGIN_TOKEN_EXPIRE_HOUR + Const.EMPTY;
            case LOGIN_FAILURE_LOCK_THRESHOLD:
            case STATISTICS_CACHE_EXPIRE:
                return Const.N_5 + Const.EMPTY;
            case LOGIN_TOKEN_AUTO_RENEW_THRESHOLD:
                return Const.N_2 + Const.EMPTY;
            case FILE_CLEAN_THRESHOLD:
                return Const.DEFAULT_FILE_CLEAN_THRESHOLD + Const.EMPTY;
            case SFTP_UPLOAD_THRESHOLD:
                return Const.SFTP_UPLOAD_THRESHOLD + Const.EMPTY;
            default:
                return null;
        }
    }

    /**
     * 创建项目目录
     *
     * @param path path
     * @return path
     */
    public static String createOrionOpsPath(String path) {
        String dir = PathBuilders.getHostEnvPath(path);
        dir = Files1.getPath(dir);
        Files1.mkdirs(dir);
        return dir;
    }

}
