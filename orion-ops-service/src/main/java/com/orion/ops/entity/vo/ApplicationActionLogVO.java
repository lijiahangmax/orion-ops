package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 应用操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 17:52
 */
@Data
@ApiModel(value = "应用操作日志响应")
public class ApplicationActionLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "引用id")
    private Long relId;

    @ApiModelProperty(value = "操作id")
    private Long actionId;

    @ApiModelProperty(value = "操作名称")
    private String actionName;

    /**
     * @see com.orion.ops.consts.app.ActionType
     */
    @ApiModelProperty(value = "操作类型")
    private Integer actionType;

    @ApiModelProperty(value = "操作命令")
    private String actionCommand;

    /**
     * @see com.orion.ops.consts.app.ActionStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消")
    private Integer status;

    @ApiModelProperty(value = "退出码")
    private Integer exitCode;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    static {
        TypeStore.STORE.register(ApplicationActionLogDO.class, ApplicationActionLogVO.class, p -> {
            ApplicationActionLogVO vo = new ApplicationActionLogVO();
            vo.setId(p.getId());
            vo.setRelId(p.getRelId());
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
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
