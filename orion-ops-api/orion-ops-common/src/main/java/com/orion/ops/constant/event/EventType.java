package com.orion.ops.constant.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/20 22:25
 */
@Getter
@AllArgsConstructor
public enum EventType {

    // -------------------- 认证操作 --------------------

    /**
     * 登陆
     */
    LOGIN(1005, EventClassify.AUTHENTICATION, "登陆系统", "登陆系统"),

    /**
     * 登出
     */
    LOGOUT(1010, EventClassify.AUTHENTICATION, "退出系统", "退出系统"),

    /**
     * 重置密码
     */
    RESET_PASSWORD(1015, EventClassify.AUTHENTICATION, "重置密码", "重置用户 <sb>${username}</sb> 密码"),

    // -------------------- 用户操作 --------------------

    /**
     * 添加用户
     */
    ADD_USER(1105, EventClassify.USER, "添加用户", "添加用户 <sb>${username}</sb>"),

    /**
     * 修改用户信息
     */
    UPDATE_USER(1110, EventClassify.USER, "修改用户信息", "修改用户信息 <sb>${username}</sb>"),

    /**
     * 删除用户
     */
    DELETE_USER(1115, EventClassify.USER, "删除用户", "删除用户 <sb>${username}</sb>"),

    /**
     * 修改用户状态
     */
    CHANGE_USER_STATUS(1120, EventClassify.USER, "修改用户状态", "${operator}用户 <sb>${username}</sb>"),

    /**
     * 解锁用户
     */
    UNLOCK_USER(1125, EventClassify.USER, "解锁用户", "解锁用户 <sb>${username}</sb>"),

    // -------------------- 机器操作 --------------------

    /**
     * 添加机器
     */
    ADD_MACHINE(2005, EventClassify.MACHINE, "添加机器", "新建机器 <sb>${machineName}</sb>"),

    /**
     * 修改机器
     */
    UPDATE_MACHINE(2010, EventClassify.MACHINE, "修改机器", "修改机器 <sb>${machineName}</sb>"),

    /**
     * 删除机器
     */
    DELETE_MACHINE(2015, EventClassify.MACHINE, "删除机器", "删除机器 <sb>${count}</sb>台"),

    /**
     * 修改机器状态
     */
    CHANGE_MACHINE_STATUS(2020, EventClassify.MACHINE, "修改机器状态", "${operator}机器 <sb>${count}</sb>台"),

    /**
     * 复制机器
     */
    COPY_MACHINE(2025, EventClassify.MACHINE, "复制机器", "复制机器 <sb>${source}</sb> -> <sb>${target}</sb>"),

    // -------------------- 机器环境变量操作 --------------------

    /**
     * 删除机器环境变量
     */
    DELETE_MACHINE_ENV(2105, EventClassify.MACHINE_ENV, "删除机器环境变量", "删除机器环境变量 <sb>${count}</sb>个"),

    /**
     * 同步机器环境变量
     */
    SYNC_MACHINE_ENV(2110, EventClassify.MACHINE_ENV, "同步机器环境变量", "同步 <sb>${envCount}</sb>个环境变量 到 <sb>${machineCount}</sb>台机器"),

    // -------------------- 秘钥操作 --------------------

    /**
     * 新增秘钥
     */
    ADD_MACHINE_KEY(2205, EventClassify.MACHINE_KEY, "新增秘钥", "新建秘钥 <sb>${keyName}</sb>"),

    /**
     * 修改秘钥
     */
    UPDATE_MACHINE_KEY(2210, EventClassify.MACHINE_KEY, "修改秘钥", "修改秘钥 <sb>${name}</sb>"),

    /**
     * 删除秘钥
     */
    DELETE_MACHINE_KEY(2215, EventClassify.MACHINE_KEY, "删除秘钥", "删除秘钥 <sb>${count}</sb>个"),

    /**
     * 挂载秘钥
     */
    MOUNT_MACHINE_KEY(2220, EventClassify.MACHINE_KEY, "挂载秘钥", "挂载秘钥 <sb>${count}</sb>个"),

    /**
     * 卸载秘钥
     */
    DUMP_MACHINE_KEY(2225, EventClassify.MACHINE_KEY, "卸载秘钥", "卸载秘钥 <sb>${count}</sb>个"),

    /**
     * 挂载全部秘钥
     */
    MOUNT_ALL_MACHINE_KEY(2230, EventClassify.MACHINE_KEY, "挂载全部秘钥", "挂载全部秘钥"),

    /**
     * 卸载全部秘钥
     */
    DUMP_ALL_MACHINE_KEY(2235, EventClassify.MACHINE_KEY, "卸载全部秘钥", "卸载全部秘钥"),

    /**
     * 临时挂载秘钥
     */
    TEMP_MOUNT_MACHINE_KEY(2240, EventClassify.MACHINE_KEY, "临时挂载秘钥", "临时挂载秘钥"),

    // -------------------- 代理操作 --------------------

    /**
     * 新增代理
     */
    ADD_MACHINE_PROXY(2305, EventClassify.MACHINE_PROXY, "新建代理", "新建代理 <sb>${proxyHost}</sb>"),

    /**
     * 修改代理
     */
    UPDATE_MACHINE_PROXY(2310, EventClassify.MACHINE_PROXY, "修改代理", "修改代理 <sb>${host}</sb>"),

    /**
     * 删除代理
     */
    DELETE_MACHINE_PROXY(2315, EventClassify.MACHINE_PROXY, "删除代理", "删除代理 <sb>${count}</sb>个"),

    // -------------------- 机器监控 --------------------

    /**
     * 修改机器监控配置
     */
    UPDATE_MACHINE_MONITOR_CONFIG(2350, EventClassify.MACHINE_MONITOR, "修改配置", "修改 <sb>${name}</sb> 机器监控插件配置"),

    /**
     * 安装或升级机器监控插件
     */
    INSTALL_UPGRADE_MACHINE_MONITOR(2355, EventClassify.MACHINE_MONITOR, "安装/升级插件", "${operator} <sb>${name}</sb> 机器监控插件"),

    // -------------------- 终端操作 --------------------

    /**
     * 打开机器终端
     */
    OPEN_TERMINAL(2400, EventClassify.TERMINAL, "打开机器终端", "打开机器终端 <sb>${machineName}</sb>"),

    /**
     * 强制下线终端
     */
    FORCE_OFFLINE_TERMINAL(2405, EventClassify.TERMINAL, "强制下线终端", "强制下线终端 <sb>${username}</sb> <sb>${name}</sb>"),

    /**
     * 修改终端配置
     */
    UPDATE_TERMINAL_CONFIG(2410, EventClassify.TERMINAL, "修改终端配置", "修改机器终端配置 <sb>${name}</sb>"),

    /**
     * 删除终端日志
     */
    DELETE_TERMINAL_LOG(2415, EventClassify.TERMINAL, "删除终端日志", "删除终端操作日志 <sb>${count}</sb>个"),

    // -------------------- sftp 操作 --------------------

    /**
     * 打开机器 SFTP
     */
    OPEN_SFTP(2500, EventClassify.SFTP, "打开机器 SFTP", "打开机器 SFTP <sb>${machineName}</sb>"),

    /**
     * 创建文件夹
     */
    SFTP_MKDIR(2505, EventClassify.SFTP, "创建文件夹", "创建文件夹 <sb>${path}</sb>"),

    /**
     * 创建文件
     */
    SFTP_TOUCH(2510, EventClassify.SFTP, "创建文件", "创建文件 <sb>${path}</sb>"),

    /**
     * 截断文件
     */
    SFTP_TRUNCATE(2515, EventClassify.SFTP, "截断文件", "截断文件 <sb>${path}</sb>"),

    /**
     * 移动文件
     */
    SFTP_MOVE(2520, EventClassify.SFTP, "移动文件", "移动文件 <sb>${source}</sb> -> <sb>${target}</sb>"),

    /**
     * 删除文件
     */
    SFTP_REMOVE(2525, EventClassify.SFTP, "删除文件", "删除文件 <sb>${count}</sb>个"),

    /**
     * 修改文件权限
     */
    SFTP_CHMOD(2530, EventClassify.SFTP, "修改文件权限", "修改文件权限 <sb>${path}</sb> >>> <sb>${permission}</sb>"),

    /**
     * 修改文件所有者
     */
    SFTP_CHOWN(2535, EventClassify.SFTP, "修改文件所有者", "修改文件所有者 <sb>${path}</sb> >>> <sb>${uid}</sb>"),

    /**
     * 修改文件所有组
     */
    SFTP_CHGRP(2540, EventClassify.SFTP, "修改文件所有组", "修改文件所有组 <sb>${path}</sb> >>> <sb>${gid}</sb>"),

    /**
     * 上传文件
     */
    SFTP_UPLOAD(2545, EventClassify.SFTP, "上传文件", "上传 <sb>${count}</sb>个文件"),

    /**
     * 下载文件
     */
    SFTP_DOWNLOAD(2550, EventClassify.SFTP, "下载文件", "下载 <sb>${count}</sb>个文件"),

    /**
     * 打包文件
     */
    SFTP_PACKAGE(2555, EventClassify.SFTP, "打包文件", "打包 <sb>${count}</sb>个文件"),

    // -------------------- 批量执行操作 --------------------

    /**
     * 批量执行
     */
    EXEC_SUBMIT(2605, EventClassify.EXEC, "批量执行", "批量执行机器命令 <sb>${count}</sb>台"),

    /**
     * 删除执行
     */
    EXEC_DELETE(2610, EventClassify.EXEC, "删除执行", "删除机器执行命令记录 <sb>${count}</sb>个"),

    /**
     * 终止执行
     */
    EXEC_TERMINATE(2615, EventClassify.EXEC, "终止执行", "终止执行机器命令"),

    // -------------------- 日志操作 --------------------

    /**
     * 添加日志文件
     */
    ADD_TAIL_FILE(2705, EventClassify.TAIL, "添加日志文件", "添加日志文件 <sb>${aliasName}</sb>"),

    /**
     * 修改日志文件
     */
    UPDATE_TAIL_FILE(2710, EventClassify.TAIL, "修改日志文件", "修改日志文件 <sb>${name}</sb>"),

    /**
     * 删除日志文件
     */
    DELETE_TAIL_FILE(2715, EventClassify.TAIL, "删除日志文件", "删除日志文件 <sb>${count}</sb>个"),

    /**
     * 上传日志文件
     */
    UPLOAD_TAIL_FILE(2720, EventClassify.TAIL, "上传日志文件", "上传日志文件 <sb>${count}</sb>个"),

    // -------------------- 模板操作 --------------------

    /**
     * 添加模板
     */
    ADD_TEMPLATE(2805, EventClassify.TEMPLATE, "添加模板", "添加模板 <sb>${templateName}</sb>"),

    /**
     * 修改模板
     */
    UPDATE_TEMPLATE(2810, EventClassify.TEMPLATE, "修改模板", "修改模板 <sb>${name}</sb>"),

    /**
     * 删除模板
     */
    DELETE_TEMPLATE(2815, EventClassify.TEMPLATE, "删除模板", "删除模板 <sb>${count}</sb>个"),

    // -------------------- 应用操作 --------------------

    /**
     * 添加应用
     */
    ADD_APP(3005, EventClassify.APP, "添加应用", "添加应用 <sb>${appName}</sb>"),

    /**
     * 修改应用
     */
    UPDATE_APP(3010, EventClassify.APP, "修改应用", "修改应用 <sb>${name}</sb>"),

    /**
     * 删除应用
     */
    DELETE_APP(3015, EventClassify.APP, "删除应用", "删除应用 <sb>${name}</sb>"),

    /**
     * 配置应用
     */
    CONFIG_APP(3020, EventClassify.APP, "配置应用", "配置应用 <sb>${profileName}</sb> <sb>${appName}</sb>"),

    /**
     * 同步应用
     */
    SYNC_APP(3025, EventClassify.APP, "同步应用", "同步应用 <sb>${name}</sb> 到 <sb>${count}</sb>个环境"),

    /**
     * 复制应用
     */
    COPY_APP(3030, EventClassify.APP, "复制应用", "复制应用 <sb>${name}</sb>"),

    // -------------------- 环境操作 --------------------

    /**
     * 添加应用环境
     */
    ADD_PROFILE(3105, EventClassify.PROFILE, "添加应用环境", "添加应用环境 <sb>${profileName}</sb>"),

    /**
     * 修改应用环境
     */
    UPDATE_PROFILE(3110, EventClassify.PROFILE, "修改应用环境", "修改应用环境 <sb>${name}</sb>"),

    /**
     * 删除应用环境
     */
    DELETE_PROFILE(3115, EventClassify.PROFILE, "删除应用环境", "删除应用环境 <sb>${name}</sb>"),

    // -------------------- 应用环境变量操作 --------------------

    /**
     * 删除应用环境变量
     */
    DELETE_APP_ENV(3205, EventClassify.APP_ENV, "删除应用环境变量", "删除应用环境变量 <sb>${count}</sb>个"),

    /**
     * 同步应用环境变量
     */
    SYNC_APP_ENV(3210, EventClassify.APP_ENV, "同步应用环境变量", "同步 <sb>${envCount}</sb>个环境变量 到 <sb>${profileCount}</sb>个环境"),

    // -------------------- 版本仓库操作 --------------------

    /**
     * 添加版本仓库
     */
    ADD_REPOSITORY(3305, EventClassify.REPOSITORY, "添加版本仓库", "添加版本仓库 <sb>${repoName}</sb>"),

    /**
     * 初始化版本仓库
     */
    INIT_REPOSITORY(3310, EventClassify.REPOSITORY, "初始化版本仓库", "初始化版本仓库 <sb>${name}</sb>"),

    /**
     * 重新初始化版本仓库
     */
    RE_INIT_REPOSITORY(3315, EventClassify.REPOSITORY, "重新初始化版本仓库", "重新初始化版本仓库 <sb>${name}</sb>"),

    /**
     * 更新版本仓库
     */
    UPDATE_REPOSITORY(3320, EventClassify.REPOSITORY, "更新版本仓库", "更新版本仓库 <sb>${name}</sb>"),

    /**
     * 删除版本仓库
     */
    DELETE_REPOSITORY(3325, EventClassify.REPOSITORY, "删除版本仓库", "删除版本仓库 <sb>${name}</sb>"),

    /**
     * 清空版本仓库
     */
    CLEAN_REPOSITORY(3330, EventClassify.REPOSITORY, "清空版本仓库", "清空版本仓库 <sb>${name}</sb>"),

    // -------------------- 构建操作 --------------------

    /**
     * 提交应用构建
     */
    SUBMIT_BUILD(4005, EventClassify.BUILD, "提交应用构建", "提交应用构建 <sb>#${buildSeq}</sb> <sb>${profileName}</sb> <sb>${appName}</sb>"),

    /**
     * 停止应用构建
     */
    BUILD_TERMINATE(4010, EventClassify.BUILD, "停止应用构建", "停止应用构建 <sb>#${buildSeq}</sb> <sb>${profileName}</sb> <sb>${appName}</sb>"),

    /**
     * 删除应用构建
     */
    DELETE_BUILD(4015, EventClassify.BUILD, "删除应用构建", "删除应用构建记录 <sb>${count}</sb>个"),

    /**
     * 重新构建应用
     */
    SUBMIT_REBUILD(4020, EventClassify.BUILD, "重新构建应用", "重新构建应用 <sb>#${buildSeq}</sb> <sb>${profileName}</sb> <sb>${appName}</sb>"),

    // -------------------- 发布操作 --------------------

    /**
     * 提交应用发布
     */
    SUBMIT_RELEASE(5005, EventClassify.RELEASE, "提交应用发布", "提交应用发布 <sb>${releaseTitle}</sb>"),

    /**
     * 应用发布审核
     */
    AUDIT_RELEASE(5010, EventClassify.RELEASE, "应用发布审核", "应用发布审核${operator} <sb>${title}</sb>"),

    /**
     * 执行应用发布
     */
    RUNNABLE_RELEASE(5015, EventClassify.RELEASE, "执行应用发布", "执行应用发布 <sb>${title}</sb>"),

    /**
     * 应用回滚发布
     */
    ROLLBACK_RELEASE(5020, EventClassify.RELEASE, "应用回滚发布", "应用回滚发布 <sb>${title}</sb>"),

    /**
     * 停止应用发布
     */
    TERMINATE_RELEASE(5025, EventClassify.RELEASE, "停止应用发布", "停止应用发布 <sb>${title}</sb>"),

    /**
     * 删除应用发布
     */
    DELETE_RELEASE(5030, EventClassify.RELEASE, "删除应用发布", "删除应用发布记录 <sb>${count}</sb>个"),

    /**
     * 复制应用发布
     */
    COPY_RELEASE(5035, EventClassify.RELEASE, "复制应用发布", "复制应用发布 <sb>${releaseTitle}</sb>"),

    /**
     * 取消应用定时发布
     */
    CANCEL_TIMED_RELEASE(5040, EventClassify.RELEASE, "取消定时发布", "取消应用定时发布 <sb>${title}</sb>"),

    /**
     * 设置应用定时发布
     */
    SET_TIMED_RELEASE(5045, EventClassify.RELEASE, "设置定时发布", "设置应用定时发布 <sb>${title}</sb> -> <sb>${time}</sb>"),

    /**
     * 停止机器发布操作
     */
    TERMINATE_MACHINE_RELEASE(5050, EventClassify.RELEASE, "停止机器操作", "停止机器发布操作 <sb>${title}</sb> <sb>${machineName}</sb>"),

    /**
     * 跳过机器发布操作
     */
    SKIP_MACHINE_RELEASE(5055, EventClassify.RELEASE, "跳过机器操作", "跳过机器发布操作 <sb>${title}</sb> <sb>${machineName}</sb>"),

    // -------------------- 应用流水线 --------------------

    /**
     * 添加应用流水线
     */
    ADD_PIPELINE(5505, EventClassify.PIPELINE, "添加流水线", "添加应用流水线 <sb>${pipelineName}</sb>"),

    /**
     * 修改应用流水线
     */
    UPDATE_PIPELINE(5510, EventClassify.PIPELINE, "修改流水线", "修改应用流水线 <sb>${pipelineName}</sb>"),

    /**
     * 删除应用流水线
     */
    DELETE_PIPELINE(5515, EventClassify.PIPELINE, "删除流水线", "删除应用流水线 <sb>${count}</sb>个"),

    /**
     * 提交应用流水线任务
     */
    SUBMIT_PIPELINE_TASK(5605, EventClassify.PIPELINE, "提交执行任务", "提交应用流水线任务 <sb>${pipelineName}</sb> <sb>${execTitle}</sb>"),

    /**
     * 审核应用流水线任务
     */
    AUDIT_PIPELINE_TASK(5610, EventClassify.PIPELINE, "审核任务", "审核应用流水线任务${operator} <sb>${name}</sb> <sb>${title}</sb>"),

    /**
     * 复制应用流水线任务
     */
    COPY_PIPELINE_TASK(5615, EventClassify.PIPELINE, "复制任务", "复制应用流水线任务 <sb>${pipelineName}</sb> <sb>${execTitle}</sb>"),

    /**
     * 执行应用流水线任务
     */
    EXEC_PIPELINE_TASK(5620, EventClassify.PIPELINE, "执行任务", "执行应用流水线任务 <sb>${name}</sb> <sb>${title}</sb>"),

    /**
     * 删除应用流水线任务
     */
    DELETE_PIPELINE_TASK(5625, EventClassify.PIPELINE, "删除任务", "删除应用流水线任务 <sb>${count}</sb>个"),

    /**
     * 设置定时执行应用流水线任务
     */
    SET_PIPELINE_TIMED_TASK(5630, EventClassify.PIPELINE, "设置定时执行", "设置定时执行应用流水线任务 <sb>${name}</sb> <sb>${title}</sb> -> <sb>${time}</sb>"),

    /**
     * 取消定时执行应用流水线任务
     */
    CANCEL_PIPELINE_TIMED_TASK(5635, EventClassify.PIPELINE, "取消定时执行", "取消定时执行应用流水线任务 <sb>${name}</sb> <sb>${title}</sb>"),

    /**
     * 停止执行应用流水线任务
     */
    TERMINATE_PIPELINE_TASK(5640, EventClassify.PIPELINE, "停止执行任务", "停止执行应用流水线任务 <sb>${name}</sb> <sb>${title}</sb>"),

    /**
     * 停止执行应用流水线任务操作
     */
    TERMINATE_PIPELINE_TASK_DETAIL(5645, EventClassify.PIPELINE, "停止执行操作", "停止执行应用流水线任务部分操作 <sb>${name}</sb> <sb>${title}</sb> (<sb 0>${stage} ${appName}</sb>)"),

    /**
     * 跳过执行应用流水线任务操作
     */
    SKIP_PIPELINE_TASK_DETAIL(5650, EventClassify.PIPELINE, "跳过执行操作", "跳过执行应用流水线任务部分操作 <sb>${name}</sb> <sb>${title}</sb> (<sb 0>${stage} ${appName}</sb>)"),

    // -------------------- 系统环境变量操作 --------------------

    /**
     * 添加系统环境变量
     */
    ADD_SYSTEM_ENV(6005, EventClassify.SYSTEM_ENV, "添加系统环境变量", "添加系统环境变量 <sb>${attrKey}</sb>"),

    /**
     * 修改系统环境变量
     */
    UPDATE_SYSTEM_ENV(6010, EventClassify.SYSTEM_ENV, "修改系统环境变量", "修改系统环境变量 <sb>${attrKey}</sb>"),

    /**
     * 删除系统环境变量
     */
    DELETE_SYSTEM_ENV(6015, EventClassify.SYSTEM_ENV, "删除系统环境变量", "删除系统环境变量 <sb>${count}</sb>个"),

    /**
     * 保存系统环境变量
     */
    SAVE_SYSTEM_ENV(6020, EventClassify.SYSTEM_ENV, "保存系统环境变量", "保存系统环境变量 <sb>${count}</sb>个"),

    // -------------------- 系统操作 --------------------

    /**
     * 配置 ip 过滤器
     */
    CONFIG_IP_LIST(6105, EventClassify.SYSTEM, "配置IP过滤器", "配置 IP 过滤器"),

    /**
     * 重新进行系统统计分析
     */
    RE_ANALYSIS_SYSTEM(6110, EventClassify.SYSTEM, "系统统计分析", "重新进行系统统计分析"),

    /**
     * 清理系统文件
     */
    CLEAN_SYSTEM_FILE(6115, EventClassify.SYSTEM, "清理系统文件", "清理系统 <sb>${label}</sb>"),

    /**
     * 修改系统配置
     */
    UPDATE_SYSTEM_OPTION(6120, EventClassify.SYSTEM, "修改系统配置", "修改系统配置项 <sb>${label}</sb> 原始值:<sb>${before}</sb> 新值:<sb>${after}</sb>"),

    // -------------------- 调度操作 --------------------

    /**
     * 添加调度任务
     */
    ADD_SCHEDULER_TASK(7105, EventClassify.SCHEDULER, "添加调度任务", "添加调度任务 <sb>${taskName}</sb>"),

    /**
     * 修改调度任务
     */
    UPDATE_SCHEDULER_TASK(7110, EventClassify.SCHEDULER, "修改调度任务", "修改调度任务 <sb>${name}</sb>"),

    /**
     * 更新调度任务状态
     */
    UPDATE_SCHEDULER_TASK_STATUS(7115, EventClassify.SCHEDULER, "更新任务状态", "${operator}调度任务 <sb>${name}</sb>"),

    /**
     * 删除调度任务
     */
    DELETE_SCHEDULER_TASK(7120, EventClassify.SCHEDULER, "删除调度任务", "删除调度任务 <sb>${name}</sb>"),

    /**
     * 手动触发调度任务
     */
    MANUAL_TRIGGER_SCHEDULER_TASK(7125, EventClassify.SCHEDULER, "手动触发任务", "手动触发调度任务 <sb>${name}</sb>"),

    /**
     * 停止调度任务
     */
    TERMINATE_ALL_SCHEDULER_TASK(7130, EventClassify.SCHEDULER, "停止任务", "停止调度任务 <sb>${name}</sb>"),

    /**
     * 停止调度任务机器操作
     */
    TERMINATE_SCHEDULER_TASK_MACHINE(7135, EventClassify.SCHEDULER, "停止机器操作", "停止调度任务机器操作 <sb>${name}</sb> <sb>${machineName}</sb>"),

    /**
     * 跳过调度任务机器操作
     */
    SKIP_SCHEDULER_TASK_MACHINE(7140, EventClassify.SCHEDULER, "跳过机器操作", "跳过调度任务机器操作 <sb>${name}</sb> <sb>${machineName}</sb>"),

    /**
     * 删除任务调度明细
     */
    DELETE_TASK_RECORD(7145, EventClassify.SCHEDULER, "删除调度明细", "删除调度任务明细 <sb>${count}</sb>个"),

    // -------------------- 数据清理 --------------------

    /**
     * 清理 批量执行数据
     */
    DATA_CLEAR_BATCH_EXEC(8105, EventClassify.DATA_CLEAR, "清理批量执行", "清理批量执行数据 <sb>${count}</sb>条"),

    /**
     * 清理 终端日志
     */
    DATA_CLEAR_TERMINAL_LOG(8110, EventClassify.DATA_CLEAR, "清理终端日志", "清理终端日志 <sb>${count}</sb>条"),

    /**
     * 清理 调度记录
     */
    DATA_CLEAR_SCHEDULER_RECORD(8115, EventClassify.DATA_CLEAR, "清理任务调度", "清理任务调度记录 <sb>${count}</sb>条"),

    /**
     * 清理 应用构建
     */
    DATA_CLEAR_APP_BUILD(8120, EventClassify.DATA_CLEAR, "清理应用构建", "清理应用构建任务数据 <sb>${count}</sb>条"),

    /**
     * 清理 应用发布
     */
    DATA_CLEAR_APP_RELEASE(8125, EventClassify.DATA_CLEAR, "清理应用发布", "清理应用发布任务数据 <sb>${count}</sb>条"),

    /**
     * 清理 应用流水线
     */
    DATA_CLEAR_APP_PIPELINE(8130, EventClassify.DATA_CLEAR, "清理应用流水线", "清理应用流水线执行数据 <sb>${count}</sb>条"),

    /**
     * 清理 操作日志
     */
    DATA_CLEAR_EVENT_LOG(8140, EventClassify.DATA_CLEAR, "清理操作日志", "清理操作日志 <sb>${count}</sb>条"),

    // -------------------- 数据导入 --------------------

    /**
     * 导入机器信息
     */
    DATA_IMPORT_MACHINE(8305, EventClassify.DATA_IMPORT, "导入机器信息", "批量导入机器信息"),

    /**
     * 导入机器代理
     */
    DATA_IMPORT_MACHINE_PROXY(8310, EventClassify.DATA_IMPORT, "导入机器代理", "批量导入机器代理"),

    /**
     * 导入日志文件
     */
    DATA_IMPORT_TAIL_FILE(8315, EventClassify.DATA_IMPORT, "导入日志文件", "批量导入日志文件"),

    /**
     * 导入应用环境
     */
    DATA_IMPORT_PROFILE(8320, EventClassify.DATA_IMPORT, "导入应用环境", "批量导入应用环境"),

    /**
     * 导入应用信息
     */
    DATA_IMPORT_APPLICATION(8325, EventClassify.DATA_IMPORT, "导入应用信息", "批量导入应用信息"),

    /**
     * 导入版本仓库
     */
    DATA_IMPORT_REPOSITORY(8330, EventClassify.DATA_IMPORT, "导入版本仓库", "批量导入应用版本仓库"),

    /**
     * 导入日志文件
     */
    DATA_IMPORT_COMMAND_TEMPLATE(8335, EventClassify.DATA_IMPORT, "导入命令模板", "批量导入命令模板"),

    // -------------------- 数据导出 --------------------

    /**
     * 导出机器信息
     */
    DATA_EXPORT_MACHINE(8505, EventClassify.DATA_EXPORT, "导出机器信息", "导出机器信息 <sb>${count}</sb>条"),

    /**
     * 导出机器代理
     */
    DATA_EXPORT_MACHINE_PROXY(8510, EventClassify.DATA_EXPORT, "导出机器代理", "导出机器代理 <sb>${count}</sb>条"),

    /**
     * 导出机器终端日志
     */
    DATA_EXPORT_TERMINAL_LOG(8515, EventClassify.DATA_EXPORT, "导出终端日志", "导出机器终端日志 <sb>${count}</sb>条"),

    /**
     * 导出日志文件
     */
    DATA_EXPORT_TAIL_FILE(8520, EventClassify.DATA_EXPORT, "导出日志文件", "导出日志文件 <sb>${count}</sb>条"),

    /**
     * 导出应用环境
     */
    DATA_EXPORT_PROFILE(8550, EventClassify.DATA_EXPORT, "导出应用环境", "导出应用环境 <sb>${count}</sb>条"),

    /**
     * 导出应用信息
     */
    DATA_EXPORT_APPLICATION(8555, EventClassify.DATA_EXPORT, "导出应用信息", "导出应用信息 <sb>${count}</sb>条"),

    /**
     * 导出应用仓库
     */
    DATA_EXPORT_REPOSITORY(8560, EventClassify.DATA_EXPORT, "导出应用仓库", "导出应用版本仓库信息 <sb>${count}</sb>条"),

    /**
     * 导出命令模板
     */
    DATA_EXPORT_COMMAND_TEMPLATE(8605, EventClassify.DATA_EXPORT, "导出命令模板", "导出命令模板 <sb>${count}</sb>条"),

    /**
     * 导出操作日志
     */
    DATA_EXPORT_EVENT_LOG(8615, EventClassify.DATA_EXPORT, "导出操作日志", "导出操作日志 <sb>${count}</sb>条"),

    ;

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 分类
     */
    private final EventClassify classify;

    /**
     * label
     */
    private final String label;

    /**
     * 模板
     */
    private final String template;

    public static EventType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (EventType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
