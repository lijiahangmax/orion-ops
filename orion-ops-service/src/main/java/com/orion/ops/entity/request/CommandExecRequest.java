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
     * 执行机器id
     */
    private Long machineId;

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
     * 关联id
     */
    private Long relId;

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
