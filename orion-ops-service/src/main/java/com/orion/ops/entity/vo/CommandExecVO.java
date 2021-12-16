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
     * 引用id
     */
    private Long relId;

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
     * 执行机器名称
     */
    private String machineName;

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

    /**
     * 使用时间 ms
     */
    private Long used;

    /**
     * 使用时间
     */
    private String keepTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private String createTimeAgo;

    static {
        TypeStore.STORE.register(CommandExecDO.class, CommandExecVO.class, p -> {
            CommandExecVO vo = new CommandExecVO();
            Date startDate = p.getStartDate();
            Date endDate = p.getEndDate();
            Date createTime = p.getCreateTime();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setUsername(p.getUserName());
            vo.setRelId(p.getRelId());
            vo.setType(p.getExecType());
            vo.setStatus(p.getExecStatus());
            vo.setMachineId(p.getMachineId());
            vo.setExitCode(p.getExitCode());
            vo.setCommand(p.getExecCommand());
            vo.setDescription(p.getDescription());
            vo.setStartDate(startDate);
            vo.setEndDate(endDate);
            vo.setCreateTime(createTime);
            Optional.ofNullable(startDate).map(Dates::ago).ifPresent(vo::setStartDateAgo);
            Optional.ofNullable(endDate).map(Dates::ago).ifPresent(vo::setEndDateAgo);
            Optional.ofNullable(createTime).map(Dates::ago).ifPresent(vo::setCreateTimeAgo);
            if (startDate != null && endDate != null) {
                vo.setUsed(endDate.getTime() - startDate.getTime());
                vo.setKeepTime(Dates.interval(vo.getUsed(), false, "d", "h", "m", "s"));
            }
            return vo;
        });
    }

}
