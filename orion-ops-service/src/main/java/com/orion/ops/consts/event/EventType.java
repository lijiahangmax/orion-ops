package com.orion.ops.consts.event;

import com.alibaba.fastjson.JSON;
import com.orion.lang.collect.MutableMap;
import com.orion.ops.entity.domain.UserEventLogDO;
import com.orion.ops.utils.EventLogUtils;
import com.orion.utils.Strings;
import lombok.Getter;

import java.util.Date;

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
     * 登出
     */
    RESET_PASSWORD(1015, EventClassify.AUTHENTICATION, "重置用户 ${username} 密码"),

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
    CHANGE_MACHINE_STATUS(2020, EventClassify.MACHINE, "${option}机器 ${count}台"),

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
     * 同步环境变量
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

    /**
     * 获取操作日志对象
     *
     * @return event
     */
    public UserEventLogDO getEventLog() {
        MutableMap<String, Object> map = EventParamsHolder.get();
        EventParamsHolder.remove();
        // 判断是否保存
        if (!map.getBooleanValue(EventKeys.INNER_SAVE, true)) {
            return null;
        }
        // 读取内置参数
        Long userId = map.getLong(EventKeys.INNER_USER_ID);
        if (userId == null) {
            return null;
        }
        // 设置对象
        UserEventLogDO log = new UserEventLogDO();
        log.setUserId(userId);
        log.setUsername(map.getString(EventKeys.INNER_USER_NAME));
        log.setEventClassify(classify.getClassify());
        log.setEventType(type);
        log.setLogInfo(Strings.format(map.getString(EventKeys.INNER_TEMPLATE, template), map));
        // 移除内部key
        EventLogUtils.removeInnerKeys(map);
        log.setParamsJson(JSON.toJSONString(map));
        log.setCreateTime(new Date());
        return log;
    }

}
