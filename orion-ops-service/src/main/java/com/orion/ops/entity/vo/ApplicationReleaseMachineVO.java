package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 应用发布机器响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 14:15
 */
@Data
@ApiModel(value = "应用发布机器响应")
public class ApplicationReleaseMachineVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "发布任务id")
    private Long releaseId;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "机器主机")
    private String machineHost;

    /**
     * @see com.orion.ops.constant.app.ActionType
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "操作")
    private List<ApplicationActionLogVO> actions;

    static {
        TypeStore.STORE.register(ApplicationReleaseMachineDO.class, ApplicationReleaseMachineVO.class, p -> {
            ApplicationReleaseMachineVO vo = new ApplicationReleaseMachineVO();
            vo.setId(p.getId());
            vo.setReleaseId(p.getReleaseId());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineTag(p.getMachineTag());
            vo.setMachineHost(p.getMachineHost());
            vo.setStatus(p.getRunStatus());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
