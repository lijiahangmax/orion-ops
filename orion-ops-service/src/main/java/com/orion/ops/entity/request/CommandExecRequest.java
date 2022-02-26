package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 批量执行请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 17:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommandExecRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private List<Long> idList;

    /**
     * 执行机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 执行机器id
     */
    private List<Long> machineIdList;

    /**
     * 执行主机
     */
    private String host;

    /**
     * 命令
     */
    private String command;

    /**
     * 执行人
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 状态
     *
     * @see com.orion.ops.consts.command.ExecStatus
     */
    private Integer status;

    /**
     * 类型
     *
     * @see com.orion.ops.consts.command.ExecType
     */
    private Integer type;

    /**
     * 退出码
     */
    private Integer exitCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否省略命令
     */
    private boolean omitCommand;

}
