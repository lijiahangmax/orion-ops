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

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.common.EnableType;
import cn.orionsec.ops.constant.history.HistoryOperator;
import cn.orionsec.ops.constant.history.HistoryValueType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.constant.tail.FileTailMode;
import cn.orionsec.ops.dao.SystemEnvDAO;
import cn.orionsec.ops.entity.domain.SystemEnvDO;
import cn.orionsec.ops.service.api.HistoryValueService;
import cn.orionsec.ops.utils.PathBuilders;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.io.Files1;
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
public class SystemEnvInitializeRunner implements CommandLineRunner {

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
            case SCREEN_PATH:
                return createOrionOpsPath(Const.SCREEN_PATH);
            case LOG_PATH:
                return createOrionOpsPath(Const.LOG_PATH);
            case TEMP_PATH:
                return createOrionOpsPath(Const.TEMP_PATH);
            case REPO_PATH:
                return createOrionOpsPath(Const.REPO_PATH);
            case DIST_PATH:
                return createOrionOpsPath(Const.DIST_PATH);
            case MACHINE_MONITOR_AGENT_PATH:
                return createOrionOpsPath(Const.MACHINE_MONITOR_AGENT_PATH);
            case TAIL_FILE_UPLOAD_PATH:
                return createOrionOpsPath(Const.TAIL_FILE_PATH);
            case TAIL_MODE:
                return FileTailMode.TRACKER.getMode();
            case TRACKER_DELAY_TIME:
                return Const.TRACKER_DELAY_MS + Const.EMPTY;
            case ENABLE_IP_FILTER:
            case ENABLE_WHITE_IP_LIST:
            case ENABLE_AUTO_CLEAN_FILE:
            case ALLOW_MULTIPLE_LOGIN:
            case RESUME_ENABLE_SCHEDULER_TASK:
            case TERMINAL_ACTIVE_PUSH_HEARTBEAT:
            case LOGIN_IP_BIND:
                return EnableType.DISABLED.getLabel();
            case LOGIN_FAILURE_LOCK:
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
