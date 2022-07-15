package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用发布统计机器响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/31 9:39
 */
@Data
@ApiModel(value = "应用发布统计机器响应")
public class ApplicationReleaseStatisticsMachineVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    /**
     * @see com.orion.ops.consts.app.ActionStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消")
    private Integer status;

    @ApiModelProperty(value = "机器操作时长毫秒")
    private Long used;

    @ApiModelProperty(value = "机器操作时长")
    private String usedInterval;

    @ApiModelProperty(value = "发布操作")
    private List<ApplicationStatisticsActionLogVO> actionLogs;

    static {
        TypeStore.STORE.register(ApplicationReleaseMachineDO.class, ApplicationReleaseStatisticsMachineVO.class, p -> {
            ApplicationReleaseStatisticsMachineVO vo = new ApplicationReleaseStatisticsMachineVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setStatus(p.getRunStatus());
            // 设置构建用时
            if (p.getStartTime() != null && p.getEndTime() != null) {
                long used = p.getEndTime().getTime() - p.getStartTime().getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

}
