package com.orion.ops.handler.exec;

import com.orion.ops.consts.command.ExecType;
import com.orion.remote.channel.SessionStore;
import lombok.Data;

import java.util.Date;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 23:05
 */
@Data
public class ExecHint {

    /**
     * 命令 (替换前)
     */
    private String command;

    /**
     * 引用id
     */
    private Long relId;

    /**
     * 描述
     */
    private String description;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 执行类型
     */
    private ExecType execType;

    /**
     * session
     */
    private SessionStore session;

    /**
     * 实际执行命令 (替换后) 无需设置
     */
    private String realCommand;

    /**
     * 执行用户id 无需设置
     */
    private Long userId;

    /**
     * 执行用户名 无需设置
     */
    private String username;

    /**
     * exitCode 无需设置
     */
    private Integer exitCode;

    /**
     * 开始时间 无需设置
     */
    private Date startDate;

}
