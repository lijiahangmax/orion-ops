package com.orion.ops.consts.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件分类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/23 17:40
 */
@AllArgsConstructor
@Getter
public enum EventClassify {

    /**
     * 认证操作
     */
    AUTHENTICATION(5),

    /**
     * 机器操作
     */
    MACHINE(10),

    /**
     * 机器环境变量操作
     */
    MACHINE_ENV(15),

    /**
     * 秘钥操作
     */
    MACHINE_KEY(20),

    /**
     * 代理操作
     */
    MACHINE_PROXY(25),

    /**
     * 终端操作
     */
    TERMINAL(30),

    /**
     * sftp 操作
     */
    SFTP(35),

    /**
     * 批量执行操作
     */
    EXEC(40),

    /**
     * tail 文件操作
     */
    TAIL(45),

    /**
     * 模板操作
     */
    TEMPLATE(50),

    /**
     * 用户操作
     */
    USER(55),

    /**
     * 应用操作
     */
    APP(60),

    /**
     * 环境操作
     */
    PROFILE(65),

    /**
     * 应用环境变量操作
     */
    APP_ENV(70),

    /**
     * 应用仓库操作
     */
    VCS(75),

    /**
     * 应用构建操作
     */
    BUILD(80),

    /**
     * 应用发布操作
     */
    RELEASE(85),

    /**
     * 系统环境变量操作
     */
    SYSTEM_ENV(90),

    /**
     * 系统操作
     */
    SYSTEM(95),

    /**
     * 调度操作
     */
    SCHEDULER(100),

    ;

    /**
     * 分类
     */
    private final Integer classify;

    public static EventClassify of(Integer classify) {
        if (classify == null) {
            return null;
        }
        for (EventClassify value : values()) {
            if (value.classify.equals(classify)) {
                return value;
            }
        }
        return null;
    }

}
