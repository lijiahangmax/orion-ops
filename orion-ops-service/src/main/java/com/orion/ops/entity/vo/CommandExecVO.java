package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 命令执行响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 15:28
 */
@Data
@ApiModel(value = "命令执行响应")
public class CommandExecVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "执行用户id")
    private Long userId;

    @ApiModelProperty(value = "执行用户名称")
    private String username;

    /**
     * @see com.orion.ops.consts.command.ExecType
     */
    @ApiModelProperty(value = "类型")
    private Integer type;

    /**
     * @see com.orion.ops.consts.command.ExecStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "执行机器id")
    private Long machineId;

    @ApiModelProperty(value = "执行机器名称")
    private String machineName;

    @ApiModelProperty(value = "执行机器主机")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "执行退出码")
    private Integer exitCode;

    @ApiModelProperty(value = "执行命令")
    private String command;

    @ApiModelProperty(value = "执行开始时间")
    private Date startDate;

    @ApiModelProperty(value = "执行开始时间")
    private String startDateAgo;

    @ApiModelProperty(value = "执行结束时间")
    private Date endDate;

    @ApiModelProperty(value = "执行结束时间")
    private String endDateAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
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
            vo.setType(p.getExecType());
            vo.setStatus(p.getExecStatus());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineHost(p.getMachineHost());
            vo.setMachineTag(p.getMachineTag());
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
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
