package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ReleaseMachineDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.List;

/**
 * 上线单机器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/14 20:23
 */
@Data
public class ReleaseMachineVO {

    /**
     * id
     */
    private Long id;

    /**
     * 上线单id
     */
    private Long releaseId;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 机器tag
     */
    private String machineTag;

    /**
     * 机器主机
     */
    private String machineHost;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过
     *
     * @see com.orion.ops.consts.app.ActionStatus
     */
    private Integer runStatus;

    /**
     * 操作
     */
    private List<ReleaseActionVO> actions;

    static {
        TypeStore.STORE.register(ReleaseMachineDO.class, ReleaseMachineVO.class, p -> {
            ReleaseMachineVO vo = new ReleaseMachineVO();
            vo.setId(p.getId());
            vo.setReleaseId(p.getReleaseId());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineTag(p.getMachineTag());
            vo.setMachineHost(p.getMachineHost());
            vo.setRunStatus(p.getRunStatus());
            return vo;
        });
    }

}
