package com.orion.ops.consts.event;

import lombok.Getter;

/**
 * 事件类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/20 22:25
 */
@Getter
public enum EventType {

    // -------------------- 认证操作 --------------------

    /**
     * 登陆
     */
    LOGIN(1005, EventClassify.AUTHENTICATION, "登陆系统"),

    /**
     * 登出
     */
    LOGOUT(1010, EventClassify.AUTHENTICATION, "退出系统"),

    /**
     * 重置密码
     */
    RESET_PASSWORD(1015, EventClassify.AUTHENTICATION, "重置用户 ${username} 密码"),

    // -------------------- 用户操作 --------------------

    /**
     * 添加用户
     */
    ADD_USER(1105, EventClassify.USER, "添加用户 ${username}"),

    /**
     * 修改用户信息
     */
    UPDATE_USER(1110, EventClassify.USER, "修改用户信息 ${username}"),

    /**
     * 删除用户
     */
    DELETE_USER(1115, EventClassify.USER, "删除用户 ${username}"),

    /**
     * 修改用户状态
     */
    CHANGE_USER_STATUS(1120, EventClassify.USER, "${operator}用户 ${username}"),

    // -------------------- 机器操作 --------------------

    /**
     * 添加机器
     */
    ADD_MACHINE(2005, EventClassify.MACHINE, "新建机器 ${machineName}"),

    /**
     * 修改机器
     */
    UPDATE_MACHINE(2010, EventClassify.MACHINE, "修改机器 ${machineName}"),

    /**
     * 删除机器
     */
    DELETE_MACHINE(2015, EventClassify.MACHINE, "删除机器 ${count}台"),

    /**
     * 修改机器状态
     */
    CHANGE_MACHINE_STATUS(2020, EventClassify.MACHINE, "${operator}机器 ${count}台"),

    /**
     * 复制机器
     */
    COPY_MACHINE(2025, EventClassify.MACHINE, "复制机器 ${source} -> ${target}"),

    // -------------------- 机器环境变量操作 --------------------

    /**
     * 删除机器环境变量
     */
    DELETE_MACHINE_ENV(2105, EventClassify.MACHINE_ENV, "删除机器环境变量 ${count}个"),

    /**
     * 同步机器环境变量
     */
    SYNC_MACHINE_ENV(2110, EventClassify.MACHINE_ENV, "同步 ${envCount}个环境变量 到 ${machineCount}台机器"),

    // -------------------- 秘钥操作 --------------------

    /**
     * 新增秘钥
     */
    ADD_MACHINE_KEY(2205, EventClassify.MACHINE_KEY, "新建秘钥 ${keyName}"),

    /**
     * 修改秘钥
     */
    UPDATE_MACHINE_KEY(2210, EventClassify.MACHINE_KEY, "修改秘钥 ${name}"),

    /**
     * 删除秘钥
     */
    DELETE_MACHINE_KEY(2215, EventClassify.MACHINE_KEY, "删除秘钥 ${count}个"),

    /**
     * 挂载秘钥
     */
    MOUNT_MACHINE_KEY(2220, EventClassify.MACHINE_KEY, "挂载秘钥 ${count}个"),

    /**
     * 卸载秘钥
     */
    DUMP_MACHINE_KEY(2225, EventClassify.MACHINE_KEY, "卸载秘钥 ${count}个"),

    /**
     * 挂载全部秘钥
     */
    MOUNT_ALL_MACHINE_KEY(2230, EventClassify.MACHINE_KEY, "挂载全部秘钥"),

    /**
     * 卸载全部秘钥
     */
    DUMP_ALL_MACHINE_KEY(2235, EventClassify.MACHINE_KEY, "卸载全部秘钥"),

    /**
     * 临时挂载秘钥
     */
    TEMP_MOUNT_MACHINE_KEY(2240, EventClassify.MACHINE_KEY, "临时挂载秘钥"),

    // -------------------- 代理操作 --------------------

    /**
     * 新增代理
     */
    ADD_MACHINE_PROXY(2305, EventClassify.MACHINE_PROXY, "新建代理 ${proxyHost}"),

    /**
     * 修改代理
     */
    UPDATE_MACHINE_PROXY(2310, EventClassify.MACHINE_PROXY, "修改代理 ${host}"),

    /**
     * 删除代理
     */
    DELETE_MACHINE_PROXY(2315, EventClassify.MACHINE_PROXY, "删除代理 ${count}个"),

    // -------------------- 终端操作 --------------------

    /**
     * 强制下线
     */
    FORCE_OFFLINE_TERMINAL(2405, EventClassify.TERMINAL, "强制下线终端 ${username} ${name}"),

    /**
     * 修改终端配置
     */
    UPDATE_TERMINAL_CONFIG(2410, EventClassify.TERMINAL, "修改机器终端配置 ${name}"),

    // -------------------- sftp 操作 --------------------

    /**
     * 创建文件夹
     */
    SFTP_MKDIR(2505, EventClassify.SFTP, "创建文件夹 ${path}"),

    /**
     * 创建文件
     */
    SFTP_TOUCH(2510, EventClassify.SFTP, "创建文件 ${path}"),

    /**
     * 截断文件
     */
    SFTP_TRUNCATE(2515, EventClassify.SFTP, "截断文件 ${path}"),

    /**
     * 移动文件
     */
    SFTP_MOVE(2520, EventClassify.SFTP, "移动文件 ${source} -> ${target}"),

    /**
     * 删除文件
     */
    SFTP_REMOVE(2525, EventClassify.SFTP, "删除文件 ${count}个 ${paths}"),

    /**
     * 修改文件权限
     */
    SFTP_CHMOD(2530, EventClassify.SFTP, "修改文件权限 ${path} >>> ${permission}"),

    /**
     * 修改文件所有者
     */
    SFTP_CHOWN(2535, EventClassify.SFTP, "修改文件所有者 ${path} >>> ${uid}"),

    /**
     * 修改文件所有组
     */
    SFTP_CHGRP(2540, EventClassify.SFTP, "修改文件所有组 ${path} >>> ${gid}"),

    /**
     * 上传文件
     */
    SFTP_UPLOAD(2545, EventClassify.SFTP, "上传 ${count}个文件"),

    /**
     * 下载文件
     */
    SFTP_DOWNLOAD(2550, EventClassify.SFTP, "下载 ${count}个文件"),

    /**
     * 打包文件
     */
    SFTP_PACKAGE(2555, EventClassify.SFTP, "打包 ${count}个文件"),

    // -------------------- 批量执行操作 --------------------

    /**
     * 批量执行
     */
    EXEC_SUBMIT(2605, EventClassify.EXEC, "批量执行机器命令 ${count}台"),

    /**
     * 删除执行
     */
    EXEC_DELETE(2610, EventClassify.EXEC, "删除执行机器命令"),

    /**
     * 终止执行
     */
    EXEC_TERMINATED(2615, EventClassify.EXEC, "终止执行机器命令"),

    // -------------------- 日志操作 --------------------

    /**
     * 添加日志文件
     */
    ADD_TAIL_FILE(2705, EventClassify.TAIL, "添加日志文件 ${aliasName}"),

    /**
     * 修改日志文件
     */
    UPDATE_TAIL_FILE(2710, EventClassify.TAIL, "修改日志文件 ${name}"),

    /**
     * 删除日志文件
     */
    DELETE_TAIL_FILE(2715, EventClassify.TAIL, "删除日志文件 ${name}"),

    // -------------------- 模板操作 --------------------

    /**
     * 添加模板
     */
    ADD_TEMPLATE(2805, EventClassify.TEMPLATE, "添加模板 ${templateName}"),

    /**
     * 修改模板
     */
    UPDATE_TEMPLATE(2810, EventClassify.TEMPLATE, "修改模板 ${name}"),

    /**
     * 删除模板
     */
    DELETE_TEMPLATE(2815, EventClassify.TEMPLATE, "删除模板 ${name}"),

    // -------------------- 应用操作 --------------------

    /**
     * 添加应用
     */
    ADD_APP(3005, EventClassify.APP, "添加应用 ${appName}"),

    /**
     * 修改应用
     */
    UPDATE_APP(3010, EventClassify.APP, "修改应用 ${name}"),

    /**
     * 删除应用
     */
    DELETE_APP(3015, EventClassify.APP, "删除应用 ${name}"),

    /**
     * 配置应用
     */
    CONFIG_APP(3020, EventClassify.APP, "配置应用 ${profileName} ${appName}"),

    /**
     * 同步应用
     */
    SYNC_APP(3025, EventClassify.APP, "同步应用 ${name} 到 ${count}个环境"),

    /**
     * 复制应用
     */
    COPY_APP(3030, EventClassify.APP, "复制应用 ${name}"),

    // -------------------- 环境操作 --------------------

    /**
     * 添加应用环境
     */
    ADD_PROFILE(3105, EventClassify.PROFILE, "添加应用环境 ${profileName}"),

    /**
     * 修改应用环境
     */
    UPDATE_PROFILE(3110, EventClassify.PROFILE, "修改应用环境 ${name}"),

    /**
     * 删除应用环境
     */
    DELETE_PROFILE(3115, EventClassify.PROFILE, "删除应用环境 ${name}"),

    // -------------------- 应用环境变量操作 --------------------

    /**
     * 删除应用环境变量
     */
    DELETE_APP_ENV(3205, EventClassify.APP_ENV, "删除应用环境变量 ${count}个"),

    /**
     * 同步应用环境变量
     */
    SYNC_APP_ENV(3210, EventClassify.APP_ENV, "同步 ${envCount}个环境变量 到 ${profileCount}个环境"),

    // -------------------- 版本仓库操作 --------------------

    /**
     * 添加版本仓库
     */
    ADD_VCS(3305, EventClassify.VCS, "添加版本仓库 ${vcsName}"),

    /**
     * 初始化版本仓库
     */
    INIT_VCS(3310, EventClassify.VCS, "初始化版本仓库 ${name}"),

    /**
     * 重新初始化版本仓库
     */
    RE_INIT_VCS(3315, EventClassify.VCS, "重新初始化版本仓库 ${name}"),

    /**
     * 更新版本仓库
     */
    UPDATE_VCS(3320, EventClassify.VCS, "更新版本仓库 ${name}"),

    /**
     * 删除版本仓库
     */
    DELETE_VCS(3325, EventClassify.VCS, "删除版本仓库 ${name}"),

    /**
     * 清空版本仓库
     */
    CLEAN_VCS(3330, EventClassify.VCS, "清空版本仓库 ${name}"),

    // -------------------- 构建操作 --------------------

    /**
     * 提交应用构建
     */
    SUBMIT_BUILD(4005, EventClassify.BUILD, "提交应用构建 #${buildSeq} ${profileName} ${appName}"),

    /**
     * 停止应用构建
     */
    BUILD_TERMINATED(4010, EventClassify.BUILD, "停止应用构建 #${buildSeq} ${profileName} ${appName}"),

    /**
     * 删除应用构建
     */
    DELETE_BUILD(4015, EventClassify.BUILD, "删除应用构建 #${buildSeq} ${profileName} ${appName}"),

    /**
     * 重新构建应用
     */
    SUBMIT_REBUILD(4020, EventClassify.BUILD, "重新构建应用 #${buildSeq} ${profileName} ${appName}"),

    // -------------------- 发布操作 --------------------

    /**
     * 提交应用发布
     */
    SUBMIT_RELEASE(5005, EventClassify.RELEASE, "提交应用发布 ${releaseTitle}"),

    /**
     * 应用发布审核
     */
    AUDIT_RELEASE(5010, EventClassify.RELEASE, "应用发布审核${operator} ${title}"),

    /**
     * 执行应用发布
     */
    RUNNABLE_RELEASE(5015, EventClassify.RELEASE, "执行应用发布 ${title}"),

    /**
     * 应用回滚发布
     */
    ROLLBACK_RELEASE(5020, EventClassify.RELEASE, "应用回滚发布 ${title}"),

    /**
     * 停止应用发布
     */
    TERMINATED_RELEASE(5025, EventClassify.RELEASE, "停止应用发布 ${title}"),

    /**
     * 删除应用发布
     */
    DELETE_RELEASE(5030, EventClassify.RELEASE, "删除应用发布 ${title}"),

    /**
     * 复制应用发布
     */
    COPY_RELEASE(5035, EventClassify.RELEASE, "复制应用发布 ${releaseTitle}"),

    ;

    EventType(Integer type, EventClassify classify, String template) {
        this.type = type;
        this.classify = classify;
        this.template = template;
    }

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 分类
     */
    private final EventClassify classify;

    /**
     * 模板
     */
    private final String template;

}
