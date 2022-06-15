package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用发布响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 9:43
 */
@Data
@ApiModel(value = "应用发布响应")
public class ApplicationReleaseListVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "发布标题")
    private String title;

    @ApiModelProperty(value = "发布描述")
    private String description;

    @ApiModelProperty(value = "构建id")
    private Long buildId;

    @ApiModelProperty(value = "构建序列")
    private Integer buildSeq;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用唯一标识")
    private String appTag;

    /**
     * @see com.orion.ops.consts.app.ReleaseType
     */
    @ApiModelProperty(value = "发布类型 10正常发布 20回滚发布")
    private Integer type;

    /**
     * @see com.orion.ops.consts.app.ReleaseStatus
     */
    @ApiModelProperty(value = "发布状态 10待审核 20审核驳回 30待发布 35待调度 40发布中 50发布完成 60发布停止 70发布失败")
    private Integer status;

    /**
     * @see com.orion.ops.consts.SerialType
     */
    @ApiModelProperty(value = "发布序列 10串行 20并行")
    private Integer serializer;

    /**
     * @see com.orion.ops.consts.ExceptionHandlerType
     */
    @ApiModelProperty(value = "异常处理 10跳过所有 20跳过错误")
    private Integer exceptionHandler;

    /**
     * @see com.orion.ops.consts.app.TimedType
     */
    @ApiModelProperty(value = "是否是定时发布 10普通发布 20定时发布")
    private Integer timedRelease;

    @ApiModelProperty(value = "定时发布时间")
    private Date timedReleaseTime;

    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "审核人名称")
    private String auditUserName;

    @ApiModelProperty(value = "审核备注")
    private String auditReason;

    @ApiModelProperty(value = "审核时间")
    private Date auditTime;

    @ApiModelProperty(value = "发布人")
    private String releaseUserName;

    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "发布机器")
    private List<ApplicationReleaseMachineVO> machines;

    static {
        TypeStore.STORE.register(ApplicationReleaseDO.class, ApplicationReleaseListVO.class, p -> {
            ApplicationReleaseListVO vo = new ApplicationReleaseListVO();
            vo.setId(p.getId());
            vo.setTitle(p.getReleaseTitle());
            vo.setDescription(p.getReleaseDescription());
            vo.setBuildId(p.getBuildId());
            vo.setBuildSeq(p.getBuildSeq());
            vo.setAppId(p.getAppId());
            vo.setAppName(p.getAppName());
            vo.setAppTag(p.getAppTag());
            vo.setType(p.getReleaseType());
            vo.setStatus(p.getReleaseStatus());
            vo.setSerializer(p.getReleaseSerialize());
            vo.setExceptionHandler(p.getExceptionHandler());
            vo.setTimedRelease(p.getTimedRelease());
            vo.setTimedReleaseTime(p.getTimedReleaseTime());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setAuditUserName(p.getAuditUserName());
            vo.setAuditReason(p.getAuditReason());
            vo.setAuditTime(p.getAuditTime());
            Date startTime = p.getReleaseStartTime();
            vo.setReleaseUserName(p.getReleaseUserName());
            vo.setReleaseTime(startTime);
            Date endTime = p.getReleaseEndTime();
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
