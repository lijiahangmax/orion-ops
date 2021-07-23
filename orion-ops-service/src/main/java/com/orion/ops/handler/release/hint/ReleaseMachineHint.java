package com.orion.ops.handler.release.hint;

import lombok.Data;

import java.io.OutputStream;

/**
 * 上线单机器配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/17 10:44
 */
@Data
public class ReleaseMachineHint {

    /**
     * id
     */
    private Long id;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器主机
     */
    private String machineHost;

    /**
     * 远程机器产物文件
     */
    private String distPath;

    /**
     * 日志路径
     */
    private String logPath;

    /**
     * 日志输出流
     */
    private OutputStream logOutputStream;

}
