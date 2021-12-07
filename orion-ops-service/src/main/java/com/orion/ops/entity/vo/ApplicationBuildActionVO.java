package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationBuildActionDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 应用构建 action 详情
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 17:52
 */
@Data
public class ApplicationBuildActionVO {

    /**
     * id
     */
    private Long id;

    /**
     * 构建id
     */
    private Long buildId;

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
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消
     *
     * @see com.orion.ops.consts.app.ActionStatus
     */
    private Integer status;

    /**
     * 退出码
     */
    private Integer exitCode;

    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 开始时间
     */
    private String startTimeAgo;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 结束时间
     */
    private String endTimeAgo;

    /**
     * 使用毫秒
     */
    private Long used;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    static {
        TypeStore.STORE.register(ApplicationBuildActionDO.class, ApplicationBuildActionVO.class, p -> {
            ApplicationBuildActionVO vo = new ApplicationBuildActionVO();
            vo.setId(p.getId());
            vo.setBuildId(p.getBuildId());
            vo.setActionId(p.getActionId());
            vo.setActionName(p.getActionName());
            vo.setActionType(p.getActionType());
            vo.setActionCommand(p.getActionCommand());
            vo.setStatus(p.getRunStatus());
            vo.setExitCode(p.getExitCode());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTime(endTime);
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
            }
            return vo;
        });
    }

}
