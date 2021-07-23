package com.orion.ops.handler.release.hint;

import lombok.Data;

import java.io.OutputStream;

/**
 * 发布操作步骤配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/17 13:15
 */
@Data
public class ReleaseActionHint {

    /**
     * id
     */
    private Long id;

    /**
     * 操作名称
     */
    private String name;

    /**
     * 操作类型
     */
    private Integer type;

    /**
     * 操作命令
     */
    private String command;

    /**
     * 操作日志文件
     */
    private String logPath;

    /**
     * 操作日志流
     */
    private OutputStream logOutputStream;

}
