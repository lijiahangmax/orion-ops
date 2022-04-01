package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.List;

/**
 * 应用发布统计机器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/31 9:39
 */
@Data
public class ApplicationReleaseStatisticsMachineVO {

    /**
     * id
     */
    private Long id;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消
     *
     * @see com.orion.ops.consts.app.ActionStatus
     */
    private Integer status;

    /**
     * 机器操作时长ms
     */
    private Long used;

    /**
     * 机器操作时长
     */
    private String usedInterval;

    /**
     * 发布操作
     */
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
