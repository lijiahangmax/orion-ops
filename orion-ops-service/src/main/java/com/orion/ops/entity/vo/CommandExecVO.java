package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 命令执行 vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 15:28
 */
@Data
public class CommandExecVO {

    /**
     * id
     */
    private Long id;

    /**
     * 执行用户id
     */
    private Long userId;

    /**
     * 执行用户名称
     */
    private String username;

    /**
     * 类型
     *
     * @see com.orion.ops.consts.command.ExecType
     */
    private Integer type;

    /**
     * 状态
     *
     * @see com.orion.ops.consts.command.ExecStatus
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 执行机器id
     */
    private Long machineId;

    /**
     * 执行机器主机
     */
    private String machineHost;

    /**
     * 执行退出码
     */
    private Integer exitCode;

    /**
     * 执行命令
     */
    private String command;

    /**
     * 日志路径
     */
    private String logPath;

    /**
     * 执行开始时间
     */
    private Date startDate;

    /**
     * 执行开始时间
     */
    private String startDateAgo;

    /**
     * 执行结束时间
     */
    private Date endDate;

    /**
     * 执行结束时间
     */
    private String endDateAgo;

    static {
        TypeStore.STORE.register(CommandExecDO.class, CommandExecVO.class, p -> {
            CommandExecVO vo = new CommandExecVO();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setUsername(p.getUserName());
            vo.setType(p.getExecType());
            vo.setStatus(p.getExecStatus());
            vo.setMachineId(p.getMachineId());
            vo.setExitCode(p.getExitCode());
            vo.setCommand(p.getExecCommand());
            vo.setDescription(p.getDescription());
            vo.setLogPath(p.getLogPath());
            vo.setStartDate(p.getStartDate());
            vo.setEndDate(p.getEndDate());
            Optional.ofNullable(p.getStartDate()).map(Dates::ago).ifPresent(vo::setStartDateAgo);
            Optional.ofNullable(p.getEndDate()).map(Dates::ago).ifPresent(vo::setEndDateAgo);
            return vo;
        });
    }

}
