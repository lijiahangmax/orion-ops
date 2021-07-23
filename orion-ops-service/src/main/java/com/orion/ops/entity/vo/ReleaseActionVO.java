package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ReleaseActionDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 上线单部署操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/14 20:27
 */
@Data
public class ReleaseActionVO {

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
     * 操作id
     */
    private Long actionId;

    /**
     * 操作名称
     */
    private String actionName;

    /**
     * 操作类型
     */
    private Integer actionType;

    /**
     * 操作命令
     */
    private String actionCommand;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过
     *
     * @see com.orion.ops.consts.app.ActionStatus
     */
    private Integer runStatus;

    /**
     * 步骤
     */
    private Integer step;

    static {
        TypeStore.STORE.register(ReleaseActionDO.class, ReleaseActionVO.class, p -> {
            ReleaseActionVO vo = new ReleaseActionVO();
            vo.setId(p.getId());
            vo.setReleaseId(p.getReleaseId());
            vo.setMachineId(p.getMachineId());
            vo.setActionId(p.getActionId());
            vo.setActionName(p.getActionName());
            vo.setActionType(p.getActionType());
            vo.setActionCommand(p.getActionCommand());
            vo.setRunStatus(p.getRunStatus());
            return vo;
        });
    }

}
